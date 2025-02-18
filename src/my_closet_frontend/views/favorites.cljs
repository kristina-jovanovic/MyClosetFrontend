(ns my-closet-frontend.views.favorites
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [my-closet-frontend.routes :as routes]))


(defonce combinations (r/atom
                        [{:id 1 :images ["https://picsum.photos/150"
                                         "https://picsum.photos/150"
                                         "https://picsum.photos/150"]
                          :description "Lorem"}
                         {:id 2 :images ["https://picsum.photos/150"
                                         "https://picsum.photos/150"]
                          :description "Lorem"}]))


(defn combination-item [{:keys [id images description]}]
      [:div.combination
       [:div.image-container
        (for [img images]
             [:img.clothing-image {:src img :key img}])]
       [:div.rating
        "Description: " description]])

(defn favorites-panel []
      [:div.home
       [:div.liked-container
        [:h2.title {:style {:color "#cb5b85"}} "Your favorite combinations"]
        (for [comb @combinations]
             ^{:key (:id comb)}
             [combination-item comb])]])

(defmethod routes/panels :favorites-panel [] [favorites-panel])

