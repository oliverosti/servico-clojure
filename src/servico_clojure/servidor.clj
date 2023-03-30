(ns servico-clojure.servidor
  (:require [io.pedestal.http :as http]
            [io.pedestal.test :as test]
            [io.pedestal.interceptor :as i]
            [servico-clojure.rotas :as rotas]
            [servico-clojure.database :as database]
            [com.stuartsierra.component :as component])
  (:use [clojure.pprint]))

(defrecord Servidor [database rotas]
  component/Lifecycle

  (start [this]
    (println "Start server http component")

    (defn assoc-store [context]
      (update context :request assoc :store (:store database)))

    (def db-interceptor
      {:name  :db-interceptor
       :enter assoc-store})

    (def service-map-base {::http/routes (:endpoints rotas)
                           ::http/port   9999
                           ::http/type   :jetty
                           ::http/join?  false})

    (def service-map
      (-> service-map-base
          (http/default-interceptors)
          (update ::http/interceptors conj (i/interceptor db-interceptor))))

    (defonce server (atom nil))

    (defn test-request [verb url]
      (test/response-for (::http/service-fn @server) verb url))

    (defn start-server []
      (reset! server (http/start (http/create-server service-map))))

    (defn stop-server []
      (http/stop @server))

    (defn restart-server []
      (stop-server)
      (start-server))

    (defn start []
      (try
        (start-server)
        (catch Exception e (println "Error when try to start server")))
      (try
        (restart-server)
        (catch Exception e (println "Error when try to restart server"))))

    (start)

    (assoc this :test-request test-request))

  (stop [this]
    (println "Stop server http component")
    (assoc this :test-request nil)))


(defn new-servidor []
  (map->Servidor {}))









