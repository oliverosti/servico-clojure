(ns servico-clojure.api-test
  (:require [clojure.test :refer :all]
            [servico-clojure.servidor :as servidor]
            [com.stuartsierra.component :as component]
            [servico-clojure.database :as database]
            [servico-clojure.rotas :as rotas])
  (:use [clojure.pprint]))

(def component-system
  (component/system-map
    :database (database/new-database)
    :rotas (rotas/new-rotas)
    :servidor (component/using (servidor/new-servidor) [:database :rotas])))

(def component-result (component/start component-system))

(pprint component-result)

(def test-request (-> component-result :servidor :test-request))

;(servidor/start)

;(test-request :get "/hello")
;(servidor/test-request :get "/hello?name=Oliver")
;(servidor/test-request :post "/tarefa?nome=Correr&status=pendente")
;(servidor/test-request :post "/tarefa?nome=Ler&status=concluido")
;(servidor/test-request :post "/tarefa?nome=Estudar&status=parado")
;(clojure.edn/read-string (:body (servidor/test-request :get "/tarefa")))
;(servidor/test-request :delete "/tarefa/d04deadf-11ee-4c7d-a162-b3de24da61d5")
;(servidor/test-request :patch "/tarefa/833f4a4e-a114-4d61-877e-d0cd3d62c022?nome=Caminhada&status=pendente")
;(servidor/test-request :get "/tarefa/6b040725-49bb-4bcc-9ece-4b5bfd55d980")


(deftest hello-world-api-test
  (testing "Hello world with name test"
    (let [path "/hello?name=Oliver"
          response (servidor/test-request :get path)
          body (:body response)]
      (is (= "Hello world Oliver" body))))
  (testing "Hello world Everybody test"
    (let [path "/hello"
          response (servidor/test-request :get path)
          body (:body response)]
      (is (= "Hello world Everybody" body)))))

(deftest tarefa-api-test
  (testing "Create new tarefa Correr test"
    (let [path "/tarefa?nome=Correr&status=pendente"
          response (servidor/test-request :post path)
          body (:body response)
          mapa-body (clojure.edn/read-string (str body))
          message (:message mapa-body)
          nome-tarefa (:nome (:tarefa mapa-body))
          status-tarefa (:status (:tarefa mapa-body))]
      (is (= "Tarefa registrada com sucesso!" message))
      (is (= "Correr" nome-tarefa))
      (is (= "pendente" status-tarefa))))
  (testing "Create new tarefa Ler test"
    (let [path "/tarefa?nome=Ler&status=concluido"
          response (servidor/test-request :post path)
          body (:body response)
          mapa-body (clojure.edn/read-string (str body))
          message (:message mapa-body)
          nome-tarefa (:nome (:tarefa mapa-body))
          status-tarefa (:status (:tarefa mapa-body))]
      (is (= "Tarefa registrada com sucesso!" message))
      (is (= "Ler" nome-tarefa))
      (is (= "concluido" status-tarefa))))
  (testing "Create new tarefa Estudar test"
    (let [path "/tarefa?nome=Estudar&status=parado"
          response (servidor/test-request :post path)
          body (:body response)
          mapa-body (clojure.edn/read-string (str body))
          message (:message mapa-body)
          nome-tarefa (:nome (:tarefa mapa-body))
          status-tarefa (:status (:tarefa mapa-body))]
      (is (= "Tarefa registrada com sucesso!" message))
      (is (= "Estudar" nome-tarefa))
      (is (= "parado" status-tarefa))))
  (testing "Listar tarefas test"
    (let [path "/tarefa"
          response (servidor/test-request :get path)
          body (:body response)
          mapa-body (clojure.edn/read-string (str body))]
      (is (= (count mapa-body) 3))))
  (testing "Get tarefa test (teste ruim)"
    (let [path "/tarefa"
          response (servidor/test-request :get path)
          body (:body response)
          mapa-body (clojure.edn/read-string (str body))
          list-of-uuids (vals mapa-body)
          tarefa (nth list-of-uuids 0)
          id-tarefa (:id tarefa)
          response-get-tarefa (servidor/test-request :get (str "/tarefa/" id-tarefa))
          body-get-tarefa (:body response-get-tarefa)
          mapa-get-tarefa (get (nth (clojure.edn/read-string (str body-get-tarefa)) 0) 1)
          nome-get-tarefa (:nome mapa-get-tarefa)
          status-get-tarefa (:status mapa-get-tarefa)
          id-get-tarefa (:id mapa-get-tarefa)]
      (is (= "Correr" nome-get-tarefa))
      (is (= "pendente" status-get-tarefa))
      (is (= id-tarefa id-get-tarefa))))
  (testing "Get tarefa test"
    (let [path "/tarefa?nome=NomeTarefaTeste&status=StatusTarefaTeste"
          response (servidor/test-request :post path)
          body (:body response)
          mapa-body (clojure.edn/read-string (str body))
          message (:message mapa-body)
          id-tarefa (:id (:tarefa mapa-body))
          nome-tarefa (:nome (:tarefa mapa-body))
          status-tarefa (:status (:tarefa mapa-body))

          path-get-tarefa (str "/tarefa/" id-tarefa)
          response-get-tarefa (servidor/test-request :get path-get-tarefa)
          body-get-tarefa (:body response-get-tarefa)
          mapa-get-tarefa (get (nth (clojure.edn/read-string (str body-get-tarefa)) 0) 1)
          nome-get-tarefa (:nome mapa-get-tarefa)
          status-get-tarefa (:status mapa-get-tarefa)
          id-get-tarefa (:id mapa-get-tarefa)]
      (is (= "Tarefa registrada com sucesso!" message))
      (is (= "NomeTarefaTeste" nome-tarefa))
      (is (= "StatusTarefaTeste" status-tarefa))
      (is (= "NomeTarefaTeste" nome-get-tarefa))
      (is (= "StatusTarefaTeste" status-get-tarefa))
      (is (= id-tarefa id-get-tarefa))))
  (testing "Delete tarefa test"
    (let [path "/tarefa?nome=NomeTarefaTeste&status=StatusTarefaTeste"
          response (servidor/test-request :post path)
          body (:body response)
          mapa-body (clojure.edn/read-string (str body))
          message (:message mapa-body)
          id-tarefa (:id (:tarefa mapa-body))

          path-delete-tarefa (str "/tarefa/" id-tarefa)
          response-delete-tarefa (servidor/test-request :delete path-delete-tarefa)
          body-delete-tarefa (:body response-delete-tarefa)
          mapa-body-delete-tarefa (clojure.edn/read-string (str body-delete-tarefa))
          message-delete-tarefa (:message mapa-body-delete-tarefa)]
      (is (= "Tarefa removida com sucesso!" message-delete-tarefa))))
  (testing "Update tarefa test"
    (let [path "/tarefa?nome=NomeTarefaTeste&status=StatusTarefaTeste"
          response (servidor/test-request :post path)
          body (:body response)
          mapa-body (clojure.edn/read-string (str body))
          message (:message mapa-body)
          id-tarefa (:id (:tarefa mapa-body))

          path-update-tarefa (str "/tarefa/" id-tarefa "?nome=NomeTarefaAlterada&status=StatusTarefaAlterada" )
          response-update-tarefa (servidor/test-request :patch path-update-tarefa)
          body-update-tarefa (:body response-update-tarefa)
          mapa-body-update-tarefa (clojure.edn/read-string (str body-update-tarefa))
          message-update-tarefa (:message mapa-body-update-tarefa)
          update-tarefa (:tarefa mapa-body-update-tarefa)
          id-update-tarefa (:id update-tarefa)
          nome-update-tarefa (:nome update-tarefa)
          status-update-tarefa (:status update-tarefa)]
      (is (= "Tarefa atualizada com sucesso!" message-update-tarefa))
      (is (= id-tarefa id-update-tarefa))
      (is (= "NomeTarefaAlterada" nome-update-tarefa))
      (is (= "StatusTarefaAlterada" status-update-tarefa)))))
