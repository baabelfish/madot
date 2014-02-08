# madot

Multiplayer AI-challenge on top of clojure and snakes.

## Usage

### Running
```
lein run
```

### Creating an AI
1. Make an AI into file `src/ai/<NAME>.clj`
2. Add the AI to `src/ai/index.clj`
   - Merge it to `:require` like so: `(:require [ai.turtle :as turtle] [ai.<NAME> :as <NAME>])`
   - Add it to the array that ai-index function returns in the same file
3. Run it

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
