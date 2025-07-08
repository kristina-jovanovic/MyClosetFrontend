(ns my-closet-frontend.views.recommendations
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [my-closet-frontend.routes :as routes]
            [my-closet-frontend.subs :as subs]
            [my-closet-frontend.events :as events]))

;(defonce clothes (r/atom
;                   ["https://picsum.photos/150"
;                    "https://picsum.photos/150"
;                    "https://picsum.photos/150"
;                    "https://picsum.photos/150"
;                    "https://picsum.photos/150"
;                    "https://picsum.photos/150"]))
;
;(defn recommendations-panel []
;      [:div.home
;       [:div.recommendations-panel.container
;        [:p.recommendations-title "Recommended combination"]
;
;        [:div.clothes-container
;         (for [row (partition-all 2 @clothes)]
;              [:div.clothes-row
;               (for [img row]
;                    [:img.clothing-item {:src "https://picsum.photos/150"}])])]
;
;        [:div.btn-container
;         [:button.btn.dislike {:on-click #(println "Dislike")} "Dislike"]
;         [:button.btn.like {:on-click #(println "Like")} "Like"]]
;        ]])
(defn recommendations-panel []
      (let [combinations (re-frame/subscribe [::subs/combinations])
            current-index (r/atom 0)]
           (re-frame/dispatch [::events/fetch-recommendations])
           (fn []
               (let [combination (when (and (seq @combinations) (< @current-index (count @combinations)))
                                       (nth @combinations @current-index))]
                    [user-id 1]
                    ;(js/console.log "current-index:" @current-index "count:" (count @combinations) "combination:" combination)

                    [:div.home
                     [:div.recommendations-panel.container
                      [:p.recommendations-title "Recommended combination"]

                      (cond
                        (nil? @combinations) [:span "Loading..."]
                        (> @current-index (dec (count @combinations))) [:span "No remaining combinations."]

                        :else
                        [:<>
                         [:div.clothes-container {:key @current-index}
                          (for [row (partition-all 2 combination)]
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
