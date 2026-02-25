# org.replikativ/logging

Unified structured logging for [replikativ](https://github.com/replikativ) built on [taoensso/trove](https://github.com/taoensso/trove).

Provides a single logging dependency across all replikativ projects with mandatory structured keyword IDs on every log call.

## Usage

Add the dependency:

```clojure
;; deps.edn
org.replikativ/logging {:mvn/version "0.1.0"}
```

Require the namespace:

```clojure
(require '[replikativ.logging :as log])
```

### Log levels

`trace`, `debug`, `info`, `warn`, `error` — all require a namespaced keyword ID as the first argument:

```clojure
;; Message only (no ID, for backwards compatibility)
(log/info "Server started on port 8080")

;; ID + message
(log/info :myapp/startup "Server started on port 8080")

;; ID + message + data
(log/info :myapp/startup "Server started" {:port 8080})

;; ID + data map (detected at compile time)
(log/info :myapp/startup {:port 8080 :env :production})
```

Using a namespaced keyword ID is recommended and follows the `:project/component` convention, e.g. `:datahike/connector`, `:konserve/filestore`, `:kabel/client`. The 1-arity form without an ID is supported for backwards compatibility.

### Error logging + throw

`raise` logs at error level and throws an `ex-info`:

```clojure
(log/raise "Invalid input: " x {:type :validation-error :input x})
```

It accepts any number of message fragments (strings or expressions) followed by a data map. Source coordinates are passed to trove automatically (CLJ-865 workaround).

### Timing

```clojure
(log/with-timing :info :myapp/query "Executed query" (run-query db q))

;; Shorthand
(log/debug-timing :myapp/index "Built index" (build-index data))
(log/info-timing :myapp/export "Exported data" (export! db))
```

Logs the duration in milliseconds as `{:duration-ms ...}` in the data map.

## Backend configuration

This library delegates to trove, which supports pluggable backends. By default, trove logs to the console. You can configure alternative backends by adding them to your classpath:

- **SLF4J** (Logback, Log4j2) — add your SLF4J implementation
- **Timbre** — add `com.taoensso/timbre` to use Timbre as the backend
- **Telemere** — add `com.taoensso/telemere` for structured observability

See the [trove documentation](https://github.com/taoensso/trove) for details on backend configuration.

## License

Copyright 2026 replikativ

Licensed under the Apache 2.0 License.
