(ns madot.core
  (:require [quil.core :refer :all]
            [ai.index :as index]
            [madot.helpers :refer :all]
            [madot.drawing :refer :all]))

(def grid-x 40)
(def grid-y 40)
(def starting-length 3)
(def directions {:left [-1 0]
                 :right [1 0]
                 :up [0 -1]
                 :down [0 1]})

(def food-density (atom 10))
(def round-number (atom 0))
(def nonpassable (atom #{}))
(def food (atom #{}))
(def wormblocks (atom #{}))
(def ai-list (zipmap
               (map inc (range))
               (->> (index/ai-index)
                    (map #(merge % {:blocks (atom (conj '() (random-point grid-x grid-y)))
                                    :size (atom starting-length)
                                    :is-alive (atom true)})))))

(defn ai-func
  [ai func]
  (let [blocks (:blocks ai)
        exec (:exec ai)
        head (first @blocks)
        allowed-size (:size ai)
        current-size (count @blocks)]
      (func ai allowed-size current-size head blocks exec)))

(defn for-ai
  "Runs a function to all AIs. If for-all is true, it will run the function for dead worms too."
  ([func for-all]
  (doseq [ai (vals ai-list)]
    (when (or for-all @(:is-alive ai))
      (ai-func ai func))))
  ([func] (for-ai func false)))

(defn- kill-ai
  ([ai]
   (swap! (:is-alive ai) (fn [x] false)))
  ([ai1 & other]
   (kill-ai ai1)
   (kill-ai other)))

(defn- check-collisions
  ;; FIXME: Use collisionmap
  "Check if collisions happen between the worms and kill them."
  []
  (doseq [ai (vals ai-list)
          aic (vals ai-list)]
    (when (and @(:is-alive ai) @(:is-alive ai))
      (let [aib @(:blocks ai)
            aicb @(:blocks aic)]
        (if (and (not= ai aic) (= (first aib) (first aicb)))
          (kill-ai ai aic)
          (when (or (in? (rest aicb) (first aib))
                    (not (on-grid? (first aib) [grid-x grid-y])))
            (kill-ai ai)))))))

(defn- update-food
  "Updates food in game"
  []
  (for-ai (fn [ai as cs head blocks exec]
            (when (and (< cs @as) (contains? @food head))
              (swap! as inc)
              (swap! food #(disj % head)))) false)
  (when (zero? (mod @round-number @food-density))
    (swap! food #(conj % (random-point grid-x grid-y)))))

(defn- update-collisionmap
  []
  (swap! wormblocks (fn [x] #{}))
  (for-ai (fn [ai as cs head blocks exec]
            (doseq [block @blocks]
              (swap! wormblocks #(conj % block))))))

(defn- update-ai []
  (for-ai (fn [ai as cs head blocks exec]
            (let [move (exec @as cs head @blocks)]
              (if (contains? (set (keys directions)) move)
                (let [newpos (vecoper head (move directions) +)]
                  (swap! blocks #(conj % newpos))
                  (when (>= cs @as)
                    (swap! blocks butlast)))
                (kill-ai ai)))))
  (update-collisionmap)
  (check-collisions))

(defn- game
  "Basic game-loop with drawing and updating."
  []
  (swap! round-number inc)
  (update-food)
  (draw-game grid-x grid-y ai-list @round-number @food)
  (update-ai))

(defn -main []
  (get-sketch grid-x grid-y game)
  (println "Matomähinä käynnis!"))
