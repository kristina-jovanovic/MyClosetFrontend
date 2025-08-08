(ns my-closet-frontend.views.favorites
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [my-closet-frontend.routes :as routes]
            [my-closet-frontend.subs :as subs]
            [my-closet-frontend.events :as events]
            [clojure.string :as str]))

(defn normalize-combination [combination clothes]
      (cond
        ; kombinacija je lista komada
        (and (sequential? combination)
             (map? (first combination))
             (:photo (first combination)))
        (do
          ;(js/console.log "Kombinacija je direktna lista komada")
          (vec combination))

        ; kombinacija iz baze
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
             "Description: " (str/join ", " (map :name unpacked))]]))

(defn favorites-panel []
      (let [favorite-combinations (re-frame/subscribe [::subs/favorite-combinations])
            clothes               (re-frame/subscribe [::subs/clothes])
            user-id (re-frame/subscribe [::subs/current-user-id])]

           ; fetch pri mountovanju
           (re-frame/dispatch [::events/fetch-clothes])
           ;(re-frame/dispatch [::events/fetch-favorite-combinations (js/parseInt user-id)])

           (fn []
               (let [clothes-loaded? (seq @clothes)
                     favs            @favorite-combinations]
                    [:div.home
                     [:div.liked-container
                      [:h2.title {:style {:color "#cb5b85"}} "Your favorite combinations"]
                      (cond
                        ; loading
                        (not clothes-loaded?)
                        [:p "Loading..."]

                        ; nema omiljenih
                        (empty? favs)
                        [:p "There are no combinations with rating 5."]

                        ; ima omiljenih
                        :else
                        (for [fav-comb favs]
                             ^{:key (:combination-id fav-comb)}
                             [combination-item fav-comb @clothes]))]]))))


(defmethod routes/panels :favorites-panel [] [favorites-panel])

