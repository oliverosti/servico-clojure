(ns servico-clojure.database
  (:require [com.stuartsierra.component :as component]))


(defrecord Database []
  component/Lifecycle

  (start [this]
    (println "Start database component")
    (assoc this :store (atom {})))

  (stop [this]
    (println "Stop database component")
    (assoc this :store nil)))

(defn new-database []
  (->Database))

;(defonce store (atom {}))
; exemplo
; {id {tarefa-id tarefa-nome tarefa-status}}
