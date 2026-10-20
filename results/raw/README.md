# Benchmark results CSV

`benchmark_results.csv` is the common raw-result file for Brute Force, KMP, k-KMP, and Aho-Corasick. Each row records one JVM execution. The file intentionally contains only its header; record only real executions, including `timeout`, `crash`, `oom`, and `wrong` rows.

The schema has the 20 required columns from the common project rules plus two Pattern Matching columns: `build_ms` and `throughput_mibs`. Do not rename, remove, or add columns without group agreement.

## Measurement protocol

- Run every measured configuration in its own JVM process. Warm up the algorithm three times on input no larger than 1 MiB; do not write warm-up results to the CSV.
- `elapsed_ms` is the end-to-end time from beginning to read the input file until matching completes. It includes file reading and searching, but excludes preprocessing, result validation, CSV writing, and plotting.
- `build_ms` records preprocessing separately, such as building a KMP prefix table or an Aho-Corasick trie, failure links, and DFA table. Record `0` when no preprocessing is required.
- Before measured Pattern Matching runs, read the file once to warm the operating-system cache and state this hot-cache protocol in the paper. Also record `algorithm=lib-read` runs with the same block size to establish the read-only baseline.
- `throughput_mibs = (n / 1,048,576) / (elapsed_ms / 1,000)`. It uses the same read-and-search interval as `elapsed_ms`; it does not include `build_ms`.
- Call `System.gc()`, reset heap peaks, then collect `heap_before_bytes` immediately before the run and `heap_peak_bytes` after it. Auxiliary heap is `heap_peak_bytes - heap_before_bytes`.
- Enable operation instrumentation only for a separate `--count` run. Leave `ops_count` blank on a timing run and leave `elapsed_ms` blank for incomplete runs.
- `status` is exactly one of `ok`, `timeout`, `crash`, `oom`, or `wrong`.

## Required and project-specific columns

| Column | Type | Meaning |
| --- | --- | --- |
| `run_id` | string | Unique execution ID, for example `RUN_0001`. |
| `rq` | string | `RQ1`, `RQ2`, `RQ3`, or `TEST`. |
| `algorithm` | string | `bf`, `kmp`, `kmp-k`, `ac`, or `lib-read`. Library baselines use the `lib-` prefix. |
| `variant` | string | Implementation variant or disabled mechanism; use `base` if none. Aho-Corasick uses `fail` or `dfa`. |
| `dataset` | string | Dataset family: `T1`, `T2`, `T3`, or `T4`. |
| `n` | integer | Main input size in bytes for this topic. |
| `params` | string | Experiment parameters as `key=value;key=value`; see the convention below. |
| `seed` | integer/string | Input-generator seed; preserve it exactly. |
| `input_sha256` | string | SHA-256 of the exact input file. |
| `generator_version` | string | Version or commit of the generator used for the input. |
| `repeat` | integer | Repeat number for the same configuration. |
| `elapsed_ms` | decimal | Measured end-to-end read-and-search time; blank if the run did not complete. |
| `heap_before_bytes` | integer | Used heap immediately before the run. |
| `heap_peak_bytes` | integer | Sum of the peak used-heap values of all heap memory pools during the measured run, in bytes. |
| `ops_count` | decimal/integer | Result of a separate `--count` run; blank on timing rows. |
| `output_check` | string | Result-validation value in the form `count=<count>;check=<check>`. |
| `status` | string | `ok`, `timeout`, `crash`, `oom`, or `wrong`. |
| `xmx` | string | JVM heap limit, for example `2g`. |
| `machine_id` | string | Stable identifier for the measurement machine. |
| `git_commit` | string | Git commit of the code measured. |
| `build_ms` | decimal | Pattern Matching preprocessing time; project-specific column. |
| `throughput_mibs` | decimal | Input MiB processed per second from `elapsed_ms`; project-specific column. |

## Parameter and operation-count convention

Put topic-specific configuration in `params`, for example:

```text
block_size_bytes=65536;pattern_set_id=PSET_001;pattern_count=10;pattern_length_min=8;pattern_length_max=16;alphabet_size=80;states_or_nodes=96
```

For Brute Force and KMP count rows, set `params` to include `counter=byte_comparisons_per_byte` and store the number of byte comparisons divided by `n` in `ops_count`.

For Aho-Corasick, make separate `--count` rows for each required metric. Include either `counter=state_transitions` or `counter=failure_link_traversals` in `params`; store the corresponding count in `ops_count`. The `dfa` variant should have zero failure-link traversals while scanning. This keeps the agreed 22-column schema while retaining both Aho-Corasick measures. When analyzing results, compare `ops_count` only between rows with the same `params.counter`; byte comparisons per byte, state transitions, and failure-link traversals have different units and meanings.

## Result validation

Do not retain all match positions. On every match, increment `count` and add `(start_position + 1) * 1000003 + pattern_id` to `check` using overflowing Java `long` addition; use `pattern_id=0` for one-pattern search. Store both values only in `output_check` as `count=<count>;check=<check>`.

Successful algorithms on the same input and pattern set must have identical `output_check`. In RQ1 and RQ2, Brute Force and KMP must agree; in RQ3, k-KMP and both Aho-Corasick variants must agree. A failed execution remains in the CSV with its status and unavailable performance values blank.
