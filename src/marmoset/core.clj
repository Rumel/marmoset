(ns marmoset.core
  (:gen-class)
  (:require [clojure.string :as s] 
            [armadillo.core :as a]
            [armadillo.calculations :as c]))

(def roic "Return on Invested Capital %")
(def sales "Revenue")
(def eps "EPS (Basic)")
(def equity "Total Equity")
(def cash "Free Cash Flow")

(defn format-percentage
  [values]
  (map (fn [value] (cond (nil? value)
                         nil
                         (= value "NEG")
                         "NEG"
                         :else
                         (str (format "%.1f" (* value 100)) "%")))
       values))

(defn read-stocks
  [filename]
  (try
    (let [contents (slurp filename)]
      (sort (s/split contents #"\n")))
    (catch Exception e (do (println "File does not exist") []))))

(defn write-line
  [line & filename]
  (if filename
    (spit (first filename) line :append true)
    (println line)))

(defn str-nil
  [string]
  (if string
    string
    "nil"))

(defn get-roic-rates
  [stock]
  (let [my-func (fn [stock year]
                    (let [value (c/value-for-year stock year roic)]
                      (if value
                          (str value "%")
                          "nil")))
        rates (map #(my-func stock %) [9 5 1 0])]
    (s/join ", " rates)))

(defn get-rates-for
  [row-name stock]
  (let [rates (c/all-rates stock row-name)
        formatted (format-percentage rates)
        nil-pass (map str-nil formatted)]
        (s/join ", " formatted)))

(defn get-numbers-for
  [stock row-name]
  (let [numbers (map #(c/value-for-year stock % row-name) [9 5 1 0])
        nil-pass (map str-nil numbers)]
    (s/join ", " nil-pass)))

(defn iterate-stocks
  [stocks filename]
  (doseq [stock stocks]
    (let [current-stock (a/get-stock stock)
          roic-rates (get-roic-rates current-stock) 
          sales-rates (get-rates-for sales current-stock) 
          sales-numbers (get-numbers-for current-stock sales)
          eps-rates (get-rates-for eps current-stock)
          eps-numbers (get-numbers-for current-stock eps)
          equity-rates (get-rates-for equity current-stock)
          equity-numbers (get-numbers-for current-stock equity)
          cash-rates (get-rates-for cash current-stock)
          cash-numbers (get-numbers-for current-stock cash)]
      (write-line (str (s/upper-case stock) "\n"
                       (str "Return on Invested Capital" ", " roic-rates) "\n"
                       (str sales ", " sales-rates) "\n"
                       (str "- ," sales-numbers) "\n"
                       (str eps ", " eps-rates) "\n"
                       (str "- ," eps-numbers) "\n"
                       (str equity ", " equity-rates) "\n"
                       (str "- ," equity-numbers) "\n"
                       (str cash ", " cash-rates) "\n" 
                       (str "- ," cash-numbers) "\n" "\n")
                  filename))))

(defn -main
  [& args]
  (if (= (count args) 2)
      (let [input-file (first args)
            output-file (last args)]
        (iterate-stocks (read-stocks input-file) output-file))
      (println "java -jar marmoset.jar [input] [output]")))
