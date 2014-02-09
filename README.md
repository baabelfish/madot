# madot

Multiplayer AI-challenge on top of clojure and snakes.

## Usage

### Running
```
$ lein run
```

### Creating an AI
1. Make an AI into file `src/ai/<NAME>.clj`
2. Add the AI to `src/ai/index.clj`
   - Add it to the array that ai-index function returns as `ai.<NAME>/init`
3. Run it

### Rules
You are only allowed to modify your own AI-file and adding the init to the
index. On top of that only information you are allowed to use, must come from
`src/ai/common.clj`.

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
