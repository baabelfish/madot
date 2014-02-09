(ns ai.index
  (:require [ai.wallhugger :as wh]
            [ai.wizard :as wizard]))

(defn ai-index []
  [wh/init wizard/init])
