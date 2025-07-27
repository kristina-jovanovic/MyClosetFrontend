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

;get recommendations

(re-frame/reg-event-fx
  ::fetch-recommendations
  (fn [_ _]
      {:http-xhrio {:method          :get
                    :uri             "http://localhost:3000/get-recommendations"
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [::fetch-recommendations-success]
                    :on-failure      [::fetch-recommendations-failure]}}))

(re-frame/reg-event-db
  ::fetch-recommendations-success
  (fn [db [_ response]]
      (js/console.log "Fetched Recommendations:" response)
      (assoc db :combinations response)))

(re-frame/reg-event-db
  ::fetch-recommendations-failure
  (fn [db [_ error]]
      (js/console.error "Failed to fetch recommendations:" error)
      db))

(re-frame/reg-event-fx
  ::insert-feedback
  (fn [_ [_ feedback]]
      {:http-xhrio {:method          :post
                    :uri             "http://localhost:3000/insert-feedback"
                    :params          feedback     ;; Clojure map
                    :format          (ajax/json-request-format)
                    :response-format (ajax/json-response-format {:keywords? true})
                    :headers         {"Content-Type" "application/json"}
                    :on-success      [::feedback-saved]
                    :on-failure      [::feedback-error]}}))



(re-frame/reg-event-db
  ::feedback-saved
  (fn [db [_ response]]
      (js/console.log "Feedback saved:" response)))

(re-frame/reg-event-db
  ::feedback-error
  (fn [db [_ error]]
      (js/console.error "Feedback not saved:" error)))