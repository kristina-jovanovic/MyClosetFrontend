(ns my-closet-frontend.views.recommendations
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [my-closet-frontend.routes :as routes]))

(defonce clothes (r/atom
                   ["https://www.vecteezy.com/free-photos"
                    "https://www.vecteezy.com/free-photos"
                    "https://www.vecteezy.com/free-photos"
                    "https://www.vecteezy.com/free-photos"
                    "https://www.vecteezy.com/free-photos"]))

(defn recommendations-panel []
      [:div.home
       [:div.recommendations-panel.container
        [:p.recommendations-title "Recommended combination"]

        [:div.clothes-container
         (for [row (partition-all 2 @clothes)]
              [:div.clothes-row
               (for [img row]
                    [:img.clothing-item {:src img}])])]

        [:div.btn-container
         [:button.btn.dislike "Dislike"]
         [:button.btn.like "Like"]]
        ]])

(defmethod routes/panels :recommendations-panel [] [recommendations-panel])
