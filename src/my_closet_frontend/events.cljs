(ns my-closet-frontend.events
  (:require
   [re-frame.core :as re-frame]
   [my-closet-frontend.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [ajax.core :as ajax]
   [ajax.edn :refer [edn-response-format]]
   [cljs.tools.reader :as reader]
   ;[cljs.reader :as reader]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [clojure.string :as str]))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))

(re-frame/reg-event-fx
  ::navigate
  (fn-traced [_ [_ handler]]
   {:navigate handler}))

(re-frame/reg-event-fx
 ::set-active-panel
 (fn-traced [{:keys [db]} [_ active-panel]]
   {:db (assoc db :active-panel active-panel)}))


;my-clothes

(re-frame/reg-event-fx
  ::fetch-clothes
  (fn [_ _]
      {:http-xhrio {:method          :get
                    :uri             "http://localhost:3000/my-clothes"
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [::fetch-clothes-success]
                    :on-failure      [::fetch-clothes-failure]}}))

(re-frame/reg-event-db
  ::fetch-clothes-success
  (fn [db [_ response]]
      (js/console.log "Fetched Clothes:" response)
      (assoc db :clothes response)))

(re-frame/reg-event-db
  ::fetch-clothes-failure
  (fn [db [_ error]]
      (js/console.error "Failed to fetch clothes:" error)
      db))
