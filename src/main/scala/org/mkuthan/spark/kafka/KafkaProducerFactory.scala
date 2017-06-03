// Copyright (C) 2011-2012 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.mkuthan.spark.kafka

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.log4j.Logger

import scala.collection.mutable

object KafkaProducerFactory {

  import scala.collection.JavaConversions._

  private val logger = Logger.getLogger(getClass)

  import org.mkuthan.spark.kafka.KafkaDsl._

  private val producers = mutable.Map[Map[String, String], KafkaProducer[K, V]]()

  import org.mkuthan.spark.kafka.KafkaDsl._

  def getOrCreateProducer(config: Map[String, String]): KafkaProducer[K, V] = {

    val defaultConfig = Map(
      "key.serializer" -> classOf[org.apache.kafka.common.serialization.ByteArraySerializer].getName,
      "value.serializer" -> classOf[org.apache.kafka.common.serialization.ByteArraySerializer].getName
    )

    val finalConfig = defaultConfig ++ config

    producers.getOrElseUpdate(finalConfig, {
      logger.info(s"Create Kafka producer , config: $finalConfig")
      val producer = new KafkaProducer[K, V](finalConfig)

      sys.addShutdownHook {
        logger.info(s"Close Kafka producer, config: $finalConfig")
        producer.close()
      }

      producer
    })
  }
}

