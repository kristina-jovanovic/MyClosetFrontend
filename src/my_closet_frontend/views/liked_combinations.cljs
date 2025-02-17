(ns my-closet-frontend.views.liked-combinations
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [my-closet-frontend.routes :as routes]))

(defonce combinations (r/atom
                        [{:id 1 :images ["https://picsum.photos/150"
                                         "https://picsum.photos/150"
                                         "https://picsum.photos/150"]
                          :rating 3}
                         {:id 2 :images ["https://picsum.photos/150"
                                         "https://picsum.photos/150"]
                          :rating 5}]))

(defn rate-combination [id new-rating]
      (swap! combinations
             (fn [combs]
                 (mapv (fn [comb]
                           (if (= (:id comb) id)
                             (assoc comb :rating new-rating)
                             comb))
                       combs))))

(defn combination-item [{:keys [id images rating]}]
      [:div.combination
       [:div.image-container
        (for [img images]
             [:img.clothing-image {:src img :key img}])]
       [:div.rating
        "Rating: "
        (for [i (range 1 6)]
             [:span {:key i
                     :class (if (<= i rating) "star filled" "star")
                     :on-click #(rate-combination id i)}
              "★"])]])

(defn liked-combinations-panel []
      [:div.home
       [:div.liked-container
       [:h2.title {:style {:color "#cb5b85"}} "Your Liked Combinations"]
       (for [comb @combinations]
            ^{:key (:id comb)}
            [combination-item comb])]])

(defmethod routes/panels :liked-combinations-panel [] [liked-combinations-panel])
