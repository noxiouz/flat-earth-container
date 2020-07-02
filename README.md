# FlatMap container

An implementation of FlatMap container (aka Boost flat_map) in Scala.

In theory it should be easier for GC to copy that map from Eden to S0/S1 and b/w S0/S1 than ordinary map.

It's very bad for modifications (O(N)), but potentialy faster on lookups as it is more cache friendly.
In Scala 2.13.x ArraySeq should be used, but I am not as good in Scala to make it happen.

# Benchmarks

Current values with comparison to `java.util.TreeMap` and `scala.collection.immutable.TreeMap`. Absolute values are meaningless unlike relative

```
[info] Benchmark                                                                    Mode  Cnt        Score       Error   Units
[info] FlatMapBencchmark.lookupAllKeysFlatMap                                       avgt    5  1734926.417 ±  5426.746   ns/op
[info] FlatMapBencchmark.lookupAllKeysFlatMap:·gc.alloc.rate                        avgt    5       85.250 ±     0.266  MB/sec
[info] FlatMapBencchmark.lookupAllKeysFlatMap:·gc.alloc.rate.norm                   avgt    5   162848.076 ±     0.010    B/op
[info] FlatMapBencchmark.lookupAllKeysFlatMap:·gc.churn.PS_Eden_Space               avgt    5       85.206 ±     8.744  MB/sec
[info] FlatMapBencchmark.lookupAllKeysFlatMap:·gc.churn.PS_Eden_Space.norm          avgt    5   162764.538 ± 16907.671    B/op
[info] FlatMapBencchmark.lookupAllKeysFlatMap:·gc.churn.PS_Survivor_Space           avgt    5        0.023 ±     0.019  MB/sec
[info] FlatMapBencchmark.lookupAllKeysFlatMap:·gc.churn.PS_Survivor_Space.norm      avgt    5       43.203 ±    36.680    B/op
[info] FlatMapBencchmark.lookupAllKeysFlatMap:·gc.count                             avgt    5      101.000              counts
[info] FlatMapBencchmark.lookupAllKeysFlatMap:·gc.time                              avgt    5       46.000                  ms
[info] FlatMapBencchmark.lookupAllKeysJavaTreeMap                                   avgt    5   977655.465 ± 22091.444   ns/op
[info] FlatMapBencchmark.lookupAllKeysJavaTreeMap:·gc.alloc.rate                    avgt    5      150.335 ±     3.370  MB/sec
[info] FlatMapBencchmark.lookupAllKeysJavaTreeMap:·gc.alloc.rate.norm               avgt    5   161824.045 ±     0.027    B/op
[info] FlatMapBencchmark.lookupAllKeysJavaTreeMap:·gc.churn.PS_Eden_Space           avgt    5      150.708 ±     8.244  MB/sec
[info] FlatMapBencchmark.lookupAllKeysJavaTreeMap:·gc.churn.PS_Eden_Space.norm      avgt    5   162223.292 ±  6937.536    B/op
[info] FlatMapBencchmark.lookupAllKeysJavaTreeMap:·gc.churn.PS_Survivor_Space       avgt    5        0.048 ±     0.048  MB/sec
[info] FlatMapBencchmark.lookupAllKeysJavaTreeMap:·gc.churn.PS_Survivor_Space.norm  avgt    5       51.882 ±    50.779    B/op
[info] FlatMapBencchmark.lookupAllKeysJavaTreeMap:·gc.count                         avgt    5      193.000              counts
[info] FlatMapBencchmark.lookupAllKeysJavaTreeMap:·gc.time                          avgt    5       94.000                  ms
[info] FlatMapBencchmark.lookupAllKeysTreeMap                                       avgt    5  1242862.975 ± 29797.255   ns/op
[info] FlatMapBencchmark.lookupAllKeysTreeMap:·gc.alloc.rate                        avgt    5      237.985 ±     5.710  MB/sec
[info] FlatMapBencchmark.lookupAllKeysTreeMap:·gc.alloc.rate.norm                   avgt    5   325664.054 ±     0.001    B/op
[info] FlatMapBencchmark.lookupAllKeysTreeMap:·gc.churn.PS_Eden_Space               avgt    5      237.374 ±     6.744  MB/sec
[info] FlatMapBencchmark.lookupAllKeysTreeMap:·gc.churn.PS_Eden_Space.norm          avgt    5   324833.062 ±  9317.296    B/op
[info] FlatMapBencchmark.lookupAllKeysTreeMap:·gc.churn.PS_Survivor_Space           avgt    5        0.047 ±     0.039  MB/sec
[info] FlatMapBencchmark.lookupAllKeysTreeMap:·gc.churn.PS_Survivor_Space.norm      avgt    5       64.350 ±    53.859    B/op
[info] FlatMapBencchmark.lookupAllKeysTreeMap:·gc.count                             avgt    5      304.000              counts
[info] FlatMapBencchmark.lookupAllKeysTreeMap:·gc.time                              avgt    5      140.000                  ms
[info] FlatMapBencchmark.lookupSeveralKeysFlatMap                                   avgt    5      208.507 ±     9.639   ns/op
[info] FlatMapBencchmark.lookupSeveralKeysFlatMap:·gc.alloc.rate                    avgt    5      209.107 ±     9.514  MB/sec
[info] FlatMapBencchmark.lookupSeveralKeysFlatMap:·gc.alloc.rate.norm               avgt    5       48.000 ±     0.001    B/op
[info] FlatMapBencchmark.lookupSeveralKeysFlatMap:·gc.churn.PS_Eden_Space           avgt    5      209.281 ±     8.225  MB/sec
[info] FlatMapBencchmark.lookupSeveralKeysFlatMap:·gc.churn.PS_Eden_Space.norm      avgt    5       48.042 ±     1.750    B/op
[info] FlatMapBencchmark.lookupSeveralKeysFlatMap:·gc.churn.PS_Survivor_Space       avgt    5        0.014 ±     0.022  MB/sec
[info] FlatMapBencchmark.lookupSeveralKeysFlatMap:·gc.churn.PS_Survivor_Space.norm  avgt    5        0.003 ±     0.005    B/op
[info] FlatMapBencchmark.lookupSeveralKeysFlatMap:·gc.count                         avgt    5      268.000              counts
[info] FlatMapBencchmark.lookupSeveralKeysFlatMap:·gc.time                          avgt    5      127.000                  ms
[info] FlatMapBencchmark.lookupSeveralKeysTreeMap                                   avgt    5      154.476 ±     1.770   ns/op
[info] FlatMapBencchmark.lookupSeveralKeysTreeMap:·gc.alloc.rate                    avgt    5       94.071 ±     1.077  MB/sec
[info] FlatMapBencchmark.lookupSeveralKeysTreeMap:·gc.alloc.rate.norm               avgt    5       16.000 ±     0.001    B/op
[info] FlatMapBencchmark.lookupSeveralKeysTreeMap:·gc.churn.PS_Eden_Space           avgt    5       94.451 ±     3.942  MB/sec
[info] FlatMapBencchmark.lookupSeveralKeysTreeMap:·gc.churn.PS_Eden_Space.norm      avgt    5       16.065 ±     0.597    B/op
[info] FlatMapBencchmark.lookupSeveralKeysTreeMap:·gc.churn.PS_Survivor_Space       avgt    5        0.019 ±     0.025  MB/sec
[info] FlatMapBencchmark.lookupSeveralKeysTreeMap:·gc.churn.PS_Survivor_Space.norm  avgt    5        0.003 ±     0.004    B/op
[info] FlatMapBencchmark.lookupSeveralKeysTreeMap:·gc.count                         avgt    5      116.000              counts
[info] FlatMapBencchmark.lookupSeveralKeysTreeMap:·gc.time                          avgt    5       57.000                  ms
```