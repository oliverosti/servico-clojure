(ns servico-clojure.main
  (:require [servico-clojure.servidor :as servidor]
            [com.stuartsierra.component :as component]
            [servico-clojure.database :as database]
            [servico-clojure.rotas :as rotas])
  (:use [clojure.pprint]))

(defn component-system []
  (component/system-map
    :database (database/new-database)
    :rotas (rotas/new-rotas)
    :servidor (component/using (servidor/new-servidor) [:database :rotas])))

(def component-result (component/start (component-system)))

(pprint component-result)

(def test-request (-> component-result :servidor :test-request ))

;(servidor/start)

(test-request :get "/hello")
(servidor/test-request :get "/hello?name=Oliver")
(servidor/test-request :post "/tarefa?nome=Correr&status=pendente")
(servidor/test-request :post "/tarefa?nome=Ler&status=concluido")
(servidor/test-request :post "/tarefa?nome=Estudar&status=parado")
(clojure.edn/read-string (:body (servidor/test-request :get "/tarefa")))
(servidor/test-request :delete "/tarefa/d04deadf-11ee-4c7d-a162-b3de24da61d5")
(servidor/test-request :patch "/tarefa/833f4a4e-a114-4d61-877e-d0cd3d62c022?nome=Caminhada&status=pendente")
(servidor/test-request :get "/tarefa/6b040725-49bb-4bcc-9ece-4b5bfd55d980")