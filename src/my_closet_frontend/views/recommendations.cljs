(ns my-closet-frontend.views.recommendations
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [my-closet-frontend.routes :as routes]))

(defonce clothes (r/atom
                   ["https://picsum.photos/150"
                    "https://picsum.photos/150"
                    "https://picsum.photos/150"
                    "https://picsum.photos/150"
                    "https://picsum.photos/150"
                    "https://picsum.photos/150"]))

(defn recommendations-panel []
      [:div.home
       [:div.recommendations-panel.container
        [:p.recommendations-title "Recommended combination"]

        [:div.clothes-container
         (for [row (partition-all 2 @clothes)]
              [:div.clothes-row
               (for [img row]
                    [:img.clothing-item {:src "https://picsum.photos/150"}])])]

        [:div.btn-container
         [:button.btn.dislike {:on-click #(println "Dislike")} "Dislike"]
         [:button.btn.like {:on-click #(println "Like")} "Like"]]
        ]])

(defmethod routes/panels :recommendations-panel [] [recommendations-panel])
