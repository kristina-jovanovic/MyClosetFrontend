(ns my-closet-frontend.views.liked-combinations
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [my-closet-frontend.routes :as routes]
            [my-closet-frontend.subs :as subs]
            [my-closet-frontend.events :as events]))

(defn normalize-combination [combination clothes]

      (cond
        ;; kombinacija je lista komada
        (and (sequential? combination)
             (map? (first combination))
             (:photo (first combination)))
        (do
          ;(js/console.log "Kombinacija je direktna lista komada")
          (vec combination))

        ;; kombinacija iz baze
        (and (map? combination)
             (string? (:pieces combination)))
        (let [ids (map #(js/parseInt %) (clojure.string/split (:pieces combination) #","))
              clothes-by-id (group-by :piece-id clothes)]
             ;(js/console.log "Kombinacija iz baze, ID-jevi:" (clj->js ids))
             (vec (map #(first (get clothes-by-id %)) ids)))

        :else
        (do
          (js/console.warn "Neprepoznat format kombinacije!" (clj->js combination))
          nil)))

(defn combination-item [combination clothes]
      (let [unpacked (normalize-combination combination clothes)
            images (map :photo unpacked)]
           [:div.combination
            [:div.image-container
             (for [img images]
                  [:img.clothing-item {:src   img :key img
                                       :style {:margin "0 10px"}}])]
            [:div.rating
             "Rating: "
             (for [i (range 1 6)]
                  [:span {:key   i
                          :class (if (<= i (:rating combination)) "star filled" "star")
                          :on-click
                          ;#(js/console.log (str "Klik na zvezdu: " i " za kombinaciju ID: " (:combination-id combination)))
                          #(re-frame/dispatch [::events/update-rating (:combination-id combination) i])
                          }
                   "★"])]
            [:div.style-label (:style combination)]]))


(defn liked-combinations-panel []
      (let [liked-combinations (re-frame/subscribe [::subs/liked-combinations])
            clothes (re-frame/subscribe [::subs/clothes])
            user-id (re-frame/subscribe [::subs/current-user-id])]

           ;; Fetch pri mountovanju
           (re-frame/dispatch [::events/fetch-clothes])
           ;(re-frame/dispatch [::events/fetch-liked-combinations (js/parseInt user-id)])

           (fn []
               (let [data-ready? (and (seq @liked-combinations) (seq @clothes))]
                    [:div.home
                     [:div.liked-container
                      [:h2.title {:style {:color "#cb5b85"}} "Your liked combinations"]
                      (if-not data-ready?
                              [:p "Loading..."]
                              (for [liked-comb @liked-combinations]
                                   ^{:key (:combination-id liked-comb)}
                                   [combination-item liked-comb @clothes]))]]))))


(defmethod routes/panels :liked-combinations-panel [] [liked-combinations-panel])
