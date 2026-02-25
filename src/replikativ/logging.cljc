(ns replikativ.logging
  "Unified structured logging for replikativ projects using trove facade.

   All log calls require a namespaced keyword ID for structured filtering.

   Provides level macros (trace, debug, info, warn, error), a raise macro
   for logging + throwing, and timing utilities.

   Usage:
     (require '[replikativ.logging :as log])

     ;; message only (for backwards compatibility)
     (log/info \"Using config\")

     ;; ID + message
     (log/info :datahike/connector \"Using config\")

     ;; ID + message + data
     (log/info :datahike/connector \"Using config\" {:config c})

     ;; ID + data only
     (log/info :datahike/connector {:config c})

     ;; Error logging + throw
     (log/raise \"Invalid input\" {:type :validation-error :input x})

     ;; Timing
     (log/with-timing :info :component/op \"msg\" (body))"
  (:require [taoensso.trove :as trove]))

(defmacro trace
  ([msg]
   `(trove/log! {:level :trace :msg ~msg}))
  ([id msg-or-data]
   (if (map? msg-or-data)
     `(trove/log! {:level :trace :id ~id :data ~msg-or-data})
     `(trove/log! {:level :trace :id ~id :msg ~msg-or-data})))
  ([id msg data]
   `(trove/log! {:level :trace :id ~id :msg ~msg :data ~data})))

(defmacro debug
  ([msg]
   `(trove/log! {:level :debug :msg ~msg}))
  ([id msg-or-data]
   (if (map? msg-or-data)
     `(trove/log! {:level :debug :id ~id :data ~msg-or-data})
     `(trove/log! {:level :debug :id ~id :msg ~msg-or-data})))
  ([id msg data]
   `(trove/log! {:level :debug :id ~id :msg ~msg :data ~data})))

(defmacro info
  ([msg]
   `(trove/log! {:level :info :msg ~msg}))
  ([id msg-or-data]
   (if (map? msg-or-data)
     `(trove/log! {:level :info :id ~id :data ~msg-or-data})
     `(trove/log! {:level :info :id ~id :msg ~msg-or-data})))
  ([id msg data]
   `(trove/log! {:level :info :id ~id :msg ~msg :data ~data})))

(defmacro warn
  ([msg]
   `(trove/log! {:level :warn :msg ~msg}))
  ([id msg-or-data]
   (if (map? msg-or-data)
     `(trove/log! {:level :warn :id ~id :data ~msg-or-data})
     `(trove/log! {:level :warn :id ~id :msg ~msg-or-data})))
  ([id msg data]
   `(trove/log! {:level :warn :id ~id :msg ~msg :data ~data})))

(defmacro error
  ([msg]
   `(trove/log! {:level :error :msg ~msg}))
  ([id msg-or-data]
   (if (map? msg-or-data)
     `(trove/log! {:level :error :id ~id :data ~msg-or-data})
     `(trove/log! {:level :error :id ~id :msg ~msg-or-data})))
  ([id msg data]
   `(trove/log! {:level :error :id ~id :msg ~msg :data ~data})))

(defmacro raise
  "Logging an error and throwing an exception with message and structured data.
   Arguments:
   - Any number of strings that describe the error
   - Last argument is a map of data that helps understanding the source of the error"
  [& fragments]
  (let [msgs (butlast fragments)
        data (last fragments)
        coords [(:line (meta &form)) (:column (meta &form))]]
    `(do
       (trove/log! {:level :error
                    :msg (str ~@(map (fn [m] (if (string? m) m (list 'pr-str m))) msgs))
                    :data ~data
                    :coords ~coords})
       (throw (ex-info (str ~@(map (fn [m] (if (string? m) m (list 'pr-str m))) msgs)) ~data)))))

(defmacro with-timing
  "Execute body and log duration at specified level. Returns the result of body."
  [level id msg & body]
  `(let [start# (System/nanoTime)
         result# (do ~@body)
         duration-ms# (/ (- (System/nanoTime) start#) 1e6)]
     (trove/log! {:level ~level
                  :id ~id
                  :msg ~msg
                  :data {:duration-ms duration-ms#}})
     result#))

(defmacro debug-timing
  "Execute body and log duration at DEBUG level."
  [id msg & body]
  `(with-timing :debug ~id ~msg ~@body))

(defmacro info-timing
  "Execute body and log duration at INFO level."
  [id msg & body]
  `(with-timing :info ~id ~msg ~@body))
