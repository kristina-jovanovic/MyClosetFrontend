(ns my-closet-frontend.views.my-clothes
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [my-closet-frontend.routes :as routes]))

(defn clothing-card [{:keys [image title type color style season]}]
  [:div.clothing-card
   [:img {:src image :alt title}]
   [:div.card-overlay
    [:h3.title {:style {:font-size "20px" }} title]
    [:p (str "Type: " type)]
    [:p (str "Color: " color)]
    [:p (str "Style: " style)]
    [:p (str "Season: " season)]]])

(defn my-clothes-panel []
  (let [clothes [{:title "Pink Blouse" :image "https://picsum.photos/150"
                  :type "top" :color "pink" :style "casual" :season "summer"}
                 {:title "White Jeans" :image "https://picsum.photos/150"
                  :type "bottom" :color "white" :style "casual" :season "summer"}
                 {:title "Floral Sandals" :image "https://picsum.photos/150"
                  :type "shoes" :color "multicolor" :style "casual" :season "summer"}
                 {:title "Little Black Dress" :image "https://picsum.photos/150"
                  :type "dress" :color "black" :style "formal" :season "all"}
                 {:title "Pastel Cardigan" :image "https://picsum.photos/150"
                  :type "top" :color "pastel" :style "casual" :season "winter"}]]
    [:div.home
     [:div.liked-container
     [:h2.title {:style {:color "#cb5b85"}} "Your clothes"]
     [:div.clothes-grid
      (map clothing-card clothes)]]]))

(defmethod routes/panels :my-clothes-panel [] [my-clothes-panel])

