(ns date-helpers
  (:use compojure
	clojure.contrib.math))

(def *date-class* (.getClass (new java.util.Date (long 0))))
(def *date-format* (new java.text.SimpleDateFormat "yyyy-MM-dd"))

(defn parse-ymd
  "Parse a date in yyyy-mm-dd format"
  [datetxt]
  (.parse *date-format* datetxt))

(defn format-ymd
  "Format a date in yyyy-mm-dd format"
  [date]
  (.format *date-format* date))

(def milliseconds-per-day 86400000)

(defn days-between
  "Number of days in the range date1 to date2"
  [date1 date2]
  (round (/ (- (.getTime date1) (.getTime date2)) milliseconds-per-day)))

 (defn parseLong [s]
   (try (Long/parseLong (str s)) (catch java.lang.Exception e 0)))
 
 (defn get-date-from-anything [value]
   "Returns a Date object. Accepts a Date, number or String."
   (cond 
     (nil? value)
       (new java.util.Date (long 0))
     (instance? *date-class* value)
       value
     (number? value)
       (new java.util.Date (* 1000 (long value)))
     true
       (new java.util.Date (* 1000 (long (parseLong (str value)))))))
 
 (import '(java.util Calendar))
 (def monthNames ["January" "February" "March" "April" "May" "June" "July" "August" "September" "October" "November" "December"])
 
 (defn input-date-field 
   "Creates a form element to input a date"
   [name value fromYear toYear]
   (let [calendar (Calendar/getInstance)
         name (.replaceAll (str name) ":" "")]
     (.setTime calendar (get-date-from-anything value))
     (html
       (str "<script>
         function changed_date_" name "()
         {
           function $(id) { return document.getElementById(id); }
           var date = new Date(parseInt($('year_" name "').value), parseInt($('month_" name "').value), parseInt($('day_" name "').value));
           $('" name "').value = date.getTime() / 1000;
           //alert('c=:' + $('" name "').value);
         }
       </script>")
       [:input {:type 'hidden :name name :id name :value value}]
       [:input {:type 'text :id (str "day_" name) :value (.get calendar Calendar/DAY_OF_MONTH) :size 1 :onchange (str "changed_date_" name "();")}] " "
       [:select {:id (str "month_" name) :onchange (str "changed_date_" name "();")}
         (reduce #(str % (html [:option {:value %2 :selected (if (= %2 (.get calendar Calendar/MONTH)) "true" nil)} (nth monthNames %2)])) "" (range 12))] " "
       [:select {:id (str "year_" name) :onchange (str "changed_date_" name "();")}
         (reduce #(str % (html [:option {:value (+ fromYear %2)} (+ fromYear %2)])) "" (range (- (inc toYear) fromYear)))] " "
   )))