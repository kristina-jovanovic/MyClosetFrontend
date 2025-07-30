(ns my-closet-frontend.views.recommendations
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [my-closet-frontend.routes :as routes]
            [my-closet-frontend.subs :as subs]
            [my-closet-frontend.events :as events]
            [clojure.string :as str]))

(defn normalize-combination [combination clothes]
      (js/console.log "NORMALIZE-COMBINATION input:" (clj->js combination))
      (cond
        ; kombinacija je lista komada
        (and (sequential? combination)
             (map? (first combination))
             (:photo (first combination)))
        (do
          (js/console.log "Kombinacija je direktna lista komada")
          (vec combination))

        ; kombinacija iz baze
        (and (map? combination)
             (string? (:pieces combination)))
        (let [ids (map #(js/parseInt %) (clojure.string/split (:pieces combination) #","))
              clothes-by-id (group-by :piece-id clothes)]
             (js/console.log "✅ Kombinacija iz baze, ID-jevi:" (clj->js ids))
             (vec (map #(first (get clothes-by-id %)) ids)))

        :else
        (do
          (js/console.warn "Neprepoznat format kombinacije!")
          nil)))

(defn recommendations-panel []
      (let [combinations   (re-frame/subscribe [::subs/combinations])
            clothes        (re-frame/subscribe [::subs/clothes])
            feedback-msg   (re-frame/subscribe [::subs/feedback-message])
            current-index  (r/atom 0)]

           ;; Fetch pri mountovanju
           (re-frame/dispatch [::events/fetch-clothes])
           (re-frame/dispatch [::events/fetch-recommendations])

           (fn []
               (let [data-ready? (and (seq @combinations) (seq @clothes))
                     combination (when (and data-ready?
                                            (< @current-index (count @combinations)))
                                       (nth @combinations @current-index))
                     unpacked (when data-ready?
                                    (normalize-combination combination @clothes))]

                    (js/console.log "COMBINATION @index" (clj->js combination))
                    (js/console.log "CLOTHES" (clj->js @clothes))
                    [:div.home
                     [:div.recommendations-panel.container

                      (if @feedback-msg
                        [:div.feedback-message
                         {:style {:padding "10px"
                                  :margin-bottom "10px"
                                  :background-color "#f0f0f0"
                                  :border-radius "5px"
                                  :text-align "center"
                                  :font-weight "bold"
                                  :color "#333"}}
                         @feedback-msg]

                        [:<>
                         [:p.recommendations-title "Recommended combination"]

                         (cond
                           (not data-ready?)
                           [:span "Loading..."]

                           (nil? unpacked)
                           [:span "Loading combination..."]

                           (> @current-index (dec (count @combinations)))
                           [:span "No remaining combinations."]

                           :else
                           [:<>
                            [:div.clothes-container {:key @current-index}
                             (for [row (partition-all 2 unpacked)]
                                  [:div.clothes-row
                                   (for [piece row]
                                        ^{:key (:piece-id piece)}
                                        [:img.clothing-item {:src (:photo piece)}])])]

                            [:div.btn-container
                             [:button.btn.dislike
                              {:on-click
                               #(do (swap! current-index inc)
                                    (re-frame/dispatch
                                      [::events/insert-feedback {:user-id 2
                                                                 :combination combination
                                                                 :opinion "dislike"}]))}
                              "Dislike"]

                             [:button.btn.like
                              {:on-click
                               #(re-frame/dispatch
                                  [::events/insert-feedback {:user-id 2
                                                             :combination combination
                                                             :opinion "like"}])}
                              "Like"]]])])]]))))



(defmethod routes/panels :recommendations-panel [] [recommendations-panel])
