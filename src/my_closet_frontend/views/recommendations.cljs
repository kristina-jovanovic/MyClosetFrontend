(ns my-closet-frontend.views.recommendations
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [my-closet-frontend.routes :as routes]
            [my-closet-frontend.subs :as subs]
            [my-closet-frontend.events :as events]
            [clojure.string :as str]))

(defn unpack-combination [combination all-clothes]
      ; pun naziv!
      (let [raw-pieces (get combination :combinations/pieces)]
           (js/console.log "Raw pieces value:" raw-pieces)
           (if (and (string? raw-pieces) (not (clojure.string/blank? raw-pieces)))
             (let [pieces-list (clojure.string/split raw-pieces #",")
                   piece-ids (->> pieces-list
                                  (map clojure.string/trim)
                                  (map #(js/parseInt % 10))
                                  (filter (complement js/isNaN)))]
                  (js/console.log "Parsed IDs:" (clj->js piece-ids))
                  (let [found (filter #(some #{(:piece-id %)} piece-ids) all-clothes)]
                       (js/console.log "Found pieces:" (clj->js found))
                       found))
             (do
               (js/console.log "pieces field missing or invalid")
               []))))


(defn recommendations-panel []
      (let [combinations   (re-frame/subscribe [::subs/combinations])
            clothes        (re-frame/subscribe [::subs/clothes]) ;; odeća sa backa
            current-index  (r/atom 0)]
           ;; Fetch odmah
           (re-frame/dispatch [::events/fetch-recommendations])
           (re-frame/dispatch [::events/fetch-clothes])

           (fn []
               (let [combination (when (and (seq @combinations)
                                            (< @current-index (count @combinations)))
                                       (nth @combinations @current-index))

                     unpacked (when combination (unpack-combination combination @clothes))]

                    [:div.home
                     [:div.recommendations-panel.container
                      [:p.recommendations-title "Recommended combination"]

                      (cond
                        (nil? @combinations)
                        [:span "Loading..."]

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
                                 (re-frame/dispatch [::events/insert-feedback {:user-id 1
                                                                               :combination combination
                                                                               :opinion "dislike"}]))}
                           "Dislike"]

                          [:button.btn.like
                           {:on-click #(re-frame/dispatch [::events/insert-feedback {:user-id 1
                                                                                     :combination combination
                                                                                     :opinion "like"}])}
                           "Like"]]])]]))))


(defmethod routes/panels :recommendations-panel [] [recommendations-panel])
