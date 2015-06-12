# spark-bug-avro

We are also experiencing this bug:
https://mail-archives.apache.org/mod_mbox/incubator-spark-user/201505.mbox/%3C1433086393609-23092.post@n3.nabble.com%3E

This is our attempt to create a reproducable test case so it can be fixed

1. Use maven to build the model
2. Run GenerateDataOne (I used sbt run)
3. Run GenerateDataTwo (I used sbt run)
4. Run ErrorExample (I used sbt run)

The resulting output should show the error condition - records with dissimilr ids that are incorrectly joined together
