(ns madot.core
  (:require [quil.core :refer :all]))

(defn setup
  []
  (smooth)
  (frame-rate 10)
  (background 0))

(defn draw
  []
  (println "Drawing frame "))

(defsketch madot
  :title "madot"
  :setup setup
  :draw draw
  :size 640 640)
