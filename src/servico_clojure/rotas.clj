(ns servico-clojure.rotas
  (:require [io.pedestal.http.route :as route]
            [com.stuartsierra.component :as component]))



(defn funcao-hello
  [request]
  {:status 200 :body (str "Hello world " (get-in request [:query-params :name] "Everybody"))})

(defn cria-tarefa-mapa [uuid nome status]
  {:id uuid :nome nome :status status})

(defn cria-tarefa [request]
  (let [uuid (java.util.UUID/randomUUID)
        nome (get-in request [:query-params :nome])
        status (get-in request [:query-params :status])
        tarefa (cria-tarefa-mapa uuid nome status)
        store (:store request)]
    (swap! store assoc uuid tarefa)
    {:status 200 :body {:message "Tarefa registrada com sucesso!"
                        :tarefa  tarefa}}))

(defn lista-tarefa [request]
  {:status 200 :body @(:store request)})

(defn remove-tarefa [request]
  (let [store (:store request)
        tarefa-id (get-in request [:path-params :id])
        tarefa-id-uuid (java.util.UUID/fromString tarefa-id)]
    (swap! store dissoc tarefa-id-uuid)
    {:status 200 :body {:message "Tarefa removida com sucesso!"}}))

(defn atualiza-tarefa [request]
  (let [tarefa-id (get-in request [:path-params :id])
        tarefa-id-uuid (java.util.UUID/fromString tarefa-id)
        nome (get-in request [:query-params :nome])
        status (get-in request [:query-params :status])
        tarefa (cria-tarefa-mapa tarefa-id-uuid nome status)
        store (:store request)]
    (swap! store assoc tarefa-id-uuid tarefa)
    {:status 200 :body {:message "Tarefa atualizada com sucesso!"
                        :tarefa  tarefa}}))

(defn busca-tarefa [request]
  (let [store (:store request)
        tarefa-id (get-in request [:path-params :id])
        tarefa-id-uuid (java.util.UUID/fromString tarefa-id)
        tarefa (filter (fn [[key value]] (= tarefa-id-uuid key)) @store)]
    {:status 200 :body tarefa}))

; com interceptor direto nas rotas
;(def routes (route/expand-routes
;              #{["/hello" :get funcao-hello :route-name :hello-world]
;                ["/tarefa" :post [db-interceptor cria-tarefa] :route-name :cria-tarefa]
;                ["/tarefa" :get [db-interceptor lista-tarefa] :route-name :lista-tarefa]
;                ["/tarefa/:id" :delete [db-interceptor remove-tarefa] :route-name :remove-tarefa]
;                ["/tarefa/:id" :patch [db-interceptor atualiza-tarefa] :route-name :atualiza-tarefa]
;                ["/tarefa/:id" :get [db-interceptor busca-tarefa] :route-name :busca-tarefa]}))

;(def routes (route/expand-routes
;              #{["/hello" :get funcao-hello :route-name :hello-world]
;                ["/tarefa" :post cria-tarefa :route-name :cria-tarefa]
;                ["/tarefa" :get lista-tarefa :route-name :lista-tarefa]
;                ["/tarefa/:id" :delete remove-tarefa :route-name :remove-tarefa]
;                ["/tarefa/:id" :patch atualiza-tarefa :route-name :atualiza-tarefa]
;                ["/tarefa/:id" :get busca-tarefa :route-name :busca-tarefa]}))


(defrecord Rotas []
  component/Lifecycle

  (start [this]
    (println "Start rotas component")
    (def routes (route/expand-routes
                  #{["/hello" :get funcao-hello :route-name :hello-world]
                    ["/tarefa" :post cria-tarefa :route-name :cria-tarefa]
                    ["/tarefa" :get lista-tarefa :route-name :lista-tarefa]
                    ["/tarefa/:id" :delete remove-tarefa :route-name :remove-tarefa]
                    ["/tarefa/:id" :patch atualiza-tarefa :route-name :atualiza-tarefa]
                    ["/tarefa/:id" :get busca-tarefa :route-name :busca-tarefa]}))
    (assoc this :endpoints routes))

  (stop [this]
    (println "Stop rotas component")
    (assoc this :endpoints nil)))


(defn new-rotas []
  (->Rotas))