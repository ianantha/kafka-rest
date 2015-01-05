/**
 * Copyright 2014 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.confluent.kafkarest;

import io.confluent.common.config.ConfigDef.Type;
import io.confluent.common.config.ConfigDef.Importance;
import io.confluent.rest.RestConfig;
import io.confluent.rest.RestConfigException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Settings for the REST proxy server.
 */
public class KafkaRestConfig extends RestConfig {
    public Time time;

    public static final String ID_CONFIG = "id";
    private static final String ID_CONFIG_DOC =
            "Unique ID for this REST server instance. This is used in generating unique IDs for consumers that do "
                    + "not specify their ID. The ID is empty by default, which makes a single server setup easier to "
                    + "get up and running, but is not safe for multi-server deployments where automatic consumer IDs "
                    + "are used.";
    public static final String ID_DEFAULT = "";

    public static final String ZOOKEEPER_CONNECT_CONFIG = "zookeeper.connect";
    private static final String ZOOKEEPER_CONNECT_DOC = "Specifies the ZooKeeper connection string in the form "
            + "hostname:port where host and port are the host and port of a ZooKeeper server. To allow connecting "
            + "through other ZooKeeper nodes when that ZooKeeper machine is down you can also specify multiple hosts "
            + "in the form hostname1:port1,hostname2:port2,hostname3:port3.\n"
            + "\n"
            + "The server may also have a ZooKeeper chroot path as part of it's ZooKeeper connection string which puts "
            + "its data under some path in the global ZooKeeper namespace. If so the consumer should use the same "
            + "chroot path in its connection string. For example to give a chroot path of /chroot/path you would give "
            + "the connection string as hostname1:port1,hostname2:port2,hostname3:port3/chroot/path.";
    public static final String ZOOKEEPER_CONNECT_DEFAULT = "localhost:2181";

    public static final String BOOTSTRAP_SERVERS_CONFIG = "bootstrap.servers";
    private static final String BOOTSTRAP_SERVERS_DOC = "A list of host/port pairs to use for establishing the "
            + "initial connection to the Kafka cluster. Data will be load " + "balanced over all servers irrespective "
            + "of which servers are specified here for bootstrapping&mdash;this list only impacts the initial hosts "
            + "used to discover the full set of servers. This list should be in the form "
            + "<code>host1:port1,host2:port2,...</code>. Since these servers are just used for the initial connection "
            + "to discover the full cluster membership (which may change dynamically), this list need not contain the "
            + "full set of servers (you may want more than one, though, in case a server is down). If no server in "
            + "this list is available sending data will fail until on becomes available.";
    public static final String BOOTSTRAP_SERVERS_DEFAULT = "localhost:9092";

    public static final String PRODUCER_THREADS_CONFIG = "producer.threads";
    private static final String PRODUCER_THREADS_DOC = "Number of threads to run produce requests on.";
    public static final String PRODUCER_THREADS_DEFAULT = "5";

    public static final String CONSUMER_ITERATOR_TIMEOUT_MS_CONFIG = "consumer.iterator.timeout.ms";
    private static final String CONSUMER_ITERATOR_TIMEOUT_MS_DOC = "Timeout for blocking consumer iterator operations. "
            + "This should be set to a small enough value that it is possible to effectively peek() on the iterator.";
    public static final String CONSUMER_ITERATOR_TIMEOUT_MS_DEFAULT = "1";

    public static final String CONSUMER_ITERATOR_BACKOFF_MS_CONFIG = "consumer.iterator.backoff.ms";
    private static final String CONSUMER_ITERATOR_BACKOFF_MS_DOC = "Amount of time to backoff when an iterator runs "
            + "out of data. If a consumer has a dedicated worker thread, this is effectively the maximum error for the "
            + "entire request timeout. It should be small enough to closely target the timeout, but large enough to "
            + "avoid busy waiting.";
    public static final String CONSUMER_ITERATOR_BACKOFF_MS_DEFAULT = "50";

    public static final String CONSUMER_REQUEST_TIMEOUT_MS_CONFIG = "consumer.request.timeout.ms";
    private static final String CONSUMER_REQUEST_TIMEOUT_MS_DOC = "The maximum total time to wait for messages for a "
            + "request if the maximum number of messages has not yet been reached.";
    public static final String CONSUMER_REQUEST_TIMEOUT_MS_DEFAULT = "1000";

    public static final String CONSUMER_REQUEST_MAX_MESSAGES_CONFIG = "consumer.request.max.messages";
    private static final String CONSUMER_REQUEST_MAX_MESSAGES_DOC = "Maximum number of messages returned in a single "
            + "request.";
    public static final String CONSUMER_REQUEST_MAX_MESSAGES_DEFAULT = "100";

    public static final String CONSUMER_THREADS_CONFIG = "consumer.threads";
    private static final String CONSUMER_THREADS_DOC = "Number of threads to run consumer requests on.";
    public static final String CONSUMER_THREADS_DEFAULT = "1";

    public static final String CONSUMER_INSTANCE_TIMEOUT_MS_CONFIG = "consumer.instance.timeout.ms";
    private static final String CONSUMER_INSTANCE_TIMEOUT_MS_DOC = "Amount of idle time before a consumer instance "
            + "is automatically destroyed.";
    public static final String CONSUMER_INSTANCE_TIMEOUT_MS_DEFAULT = "300000";

    static {
        config
                .defineOverride(RESPONSE_MEDIATYPE_PREFERRED_CONFIG, Type.LIST,
                        Versions.PREFERRED_RESPONSE_TYPES, Importance.HIGH,
                        RESPONSE_MEDIATYPE_PREFERRED_CONFIG_DOC)
                .defineOverride(RESPONSE_MEDIATYPE_DEFAULT_CONFIG, Type.STRING,
                        Versions.KAFKA_MOST_SPECIFIC_DEFAULT, Importance.HIGH,
                        RESPONSE_MEDIATYPE_DEFAULT_CONFIG_DOC)
                .define(ID_CONFIG, Type.STRING, ID_DEFAULT, Importance.HIGH, ID_CONFIG_DOC)
                .define(ZOOKEEPER_CONNECT_CONFIG, Type.STRING, ZOOKEEPER_CONNECT_DEFAULT,
                        Importance.HIGH, ZOOKEEPER_CONNECT_DOC)
                .define(BOOTSTRAP_SERVERS_CONFIG, Type.STRING, BOOTSTRAP_SERVERS_DEFAULT,
                        Importance.HIGH, BOOTSTRAP_SERVERS_DOC)
                .define(PRODUCER_THREADS_CONFIG, Type.INT, PRODUCER_THREADS_DEFAULT,
                        Importance.LOW, PRODUCER_THREADS_DOC)
                .define(CONSUMER_ITERATOR_TIMEOUT_MS_CONFIG, Type.INT, CONSUMER_ITERATOR_TIMEOUT_MS_DEFAULT,
                        Importance.LOW, CONSUMER_ITERATOR_TIMEOUT_MS_DOC)
                .define(CONSUMER_ITERATOR_BACKOFF_MS_CONFIG, Type.INT, CONSUMER_ITERATOR_BACKOFF_MS_DEFAULT,
                        Importance.LOW, CONSUMER_ITERATOR_BACKOFF_MS_DOC)
                .define(CONSUMER_REQUEST_TIMEOUT_MS_CONFIG, Type.INT, CONSUMER_REQUEST_TIMEOUT_MS_DEFAULT,
                        Importance.MEDIUM, CONSUMER_REQUEST_TIMEOUT_MS_DOC)
                .define(CONSUMER_REQUEST_MAX_MESSAGES_CONFIG, Type.INT, CONSUMER_REQUEST_MAX_MESSAGES_DEFAULT,
                        Importance.MEDIUM, CONSUMER_REQUEST_MAX_MESSAGES_DOC)
                .define(CONSUMER_THREADS_CONFIG, Type.INT, CONSUMER_THREADS_DEFAULT,
                        Importance.MEDIUM, CONSUMER_THREADS_DOC)
                .define(CONSUMER_INSTANCE_TIMEOUT_MS_CONFIG, Type.INT, CONSUMER_INSTANCE_TIMEOUT_MS_DEFAULT,
                        Importance.LOW, CONSUMER_INSTANCE_TIMEOUT_MS_DOC);
    }

    public KafkaRestConfig() throws RestConfigException {
        this(new Properties());
    }

    public KafkaRestConfig(String propsFile) throws RestConfigException {
        this(getPropsFromFile(propsFile));
    }

    public KafkaRestConfig(Properties props) throws RestConfigException {
        super(props);
        time = new SystemTime();
    }
}