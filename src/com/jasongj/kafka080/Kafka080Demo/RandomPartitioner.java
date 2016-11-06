package com.jasongj.kafka080.Kafka080Demo;

import java.util.Random;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * Created by luolang on 16/10/16.
 */
public class RandomPartitioner  implements Partitioner {

	private static int i = 0;

	public RandomPartitioner(VerifiableProperties verifiableProperties) {
		i++;
//		System.out.println(i);
	}

	@Override
	public int partition(Object key, int numPartitions) {


		Random random = new Random();
		int randomValue = Math.abs(random.nextInt());
//		System.out.println("numPartitions="+numPartitions);
//		System.out.println(this.hashCode());
		return randomValue % numPartitions;
	}
}

