Configuration Options
---------------------

Connector
^^^^^^^^^

``connection.url``
  Elasticsearch HTTP connection URL e.g. ``http://eshost:9200``.

  * Type: string
  * Importance: high

``batch.size``
  The number of records to process as a batch when writing to Elasticsearch.

  * Type: int
  * Default: 2000
  * Importance: medium

``max.buffered.records``
  The maximum number of records each task will buffer before blocking acceptance of more records. This config can be used to limit the memory usage for each task.

  * Type: int
  * Default: 20000
  * Importance: low

``linger.ms``
  Linger time in milliseconds for batching.

  Records that arrive in between request transmissions are batched into a single bulk indexing request, based on the ``batch.size`` configuration. Normally this only occurs under load when records arrive faster than they can be sent out. However it may be desirable to reduce the number of requests even under light load and benefit from bulk indexing. This setting helps accomplish that - when a pending batch is not full, rather than immediately sending it out the task will wait up to the given delay to allow other records to be added so that they can be batched into a single request.

  * Type: long
  * Default: 1
  * Importance: low

``flush.timeout.ms``
  The timeout in milliseconds to use for periodic flushing, and when waiting for buffer space to be made available by completed requests as records are added. If this timeout is exceeded the task will fail.

  * Type: long
  * Default: 10000
  * Importance: low

``max.in.flight.requests``
  The maximum number of indexing requests that can be in-flight to Elasticsearch before blocking further requests.

  * Type: int
  * Default: 5
  * Importance: medium

``max.retries``
  The maximum number of retries that are allowed for failed indexing requests. If the retry attempts are exhausted the task will fail.

  * Type: int
  * Default: 5
  * Importance: low

``retry.backoff.ms``
  How long to wait in milliseconds before attempting to retry a failed indexing request. This avoids retrying in a tight loop under failure scenarios.

  * Type: long
  * Default: 100
  * Importance: low

Data Conversion
^^^^^^^^^^^^^^^

``type.name``
  The Elasticsearch type name to use when indexing.

  * Type: string
  * Importance: high

``key.ignore``
  Whether to ignore the record key for the purpose of forming the Elasticsearch document ID. When this is set to ``true``, document IDs will be generated as the record's ``topic+partition+offset``.

  Note that this is a global config that applies to all topics, use ``topic.key.ignore`` to override as ``true`` for specific topics.

  * Type: boolean
  * Default: false
  * Importance: high

``schema.ignore``
  Whether to ignore schemas during indexing. When this is set to ``true``, the record schema will be ignored for the purpose of registering an Elasticsearch mapping. Elasticsearch will infer the mapping from the data (dynamic mapping needs to be enabled by the user).

  Note that this is a global config that applies to all topics, use ``topic.schema.ignore`` to override as ``true`` for specific topics.

  * Type: boolean
  * Default: false
  * Importance: low

``topic.index.map``
  A map from Kafka topic name to the destination Elasticsearch index, represented as a list of ``topic:index`` pairs.

  * Type: list
  * Default: ""
  * Importance: low

``topic.key.ignore``
  List of topics for which ``key.ignore`` should be ``true``.

  * Type: list
  * Default: ""
  * Importance: low

``topic.schema.ignore``
  List of topics for which ``schema.ignore`` should be ``true``.

  * Type: list
  * Default: ""
  * Importance: low
