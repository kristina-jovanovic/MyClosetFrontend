(ns my-closet-frontend.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [my-closet-frontend.events :as events]
   [my-closet-frontend.routes :as routes]
   [my-closet-frontend.views :as views]
   [my-closet-frontend.config :as config]
   [my-closet-frontend.views.navbar]
   [my-closet-frontend.views.footer]
   [my-closet-frontend.views.get-recommendation]
   ))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (routes/start!)
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
