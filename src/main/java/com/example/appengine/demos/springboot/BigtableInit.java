/*
 * Copyright 2017 Google Inc.
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

package com.example.appengine.demos.springboot;

import com.google.cloud.bigtable.hbase.BigtableConfiguration;
import com.google.cloud.bigtable.hbase1_x.BigtableConnection;
import com.google.cloud.bigtable.hbase1_x.BigtableAdmin;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;

public class BigtableInit {
  private static final String PROJECT_ID = "grass-clump-479";
  private static final String INSTANCE_ID = "node-client-performance";

  private final Connection connection;
  private final Admin admin;

  public BigtableInit() throws IOException {
    System.out.println("setting up Connection");
    connection = BigtableConfiguration.connect(PROJECT_ID, INSTANCE_ID);
    Configuration configuration = BigtableConfiguration.configure(PROJECT_ID, INSTANCE_ID);
    BigtableConnection connection = new BigtableConnection(configuration);
    admin = new BigtableAdmin(connection);
  }

  public boolean isTable(String tableNameVal) throws IOException {
    TableName tableName = TableName.valueOf(tableNameVal);
    return admin.tableExists(tableName);
  }

  public List<String> fetchRows(String tableName, String rowKey) throws IOException {
    Table table = connection.getTable(TableName.valueOf(tableName));
    Get get = new Get(rowKey.getBytes());
    return printResult(table.get(get));
  }

  public List<String> printResult(Result result) {
    List<String> data = new ArrayList<>();
    String row = new String(result.getRow());
    List<Cell> cells = result.listCells();
    for (Cell cell : cells) {
      String family = new String(cell.getFamilyArray());
      String qualifier = new String(cell.getQualifierArray());
      String value = new String(cell.getValueArray());
      String printLine =
          "Row: " + row + ", Family: " + family + ", Qualifier: " + qualifier + ", Value: " + value;
      System.out.println(printLine);
      data.add(printLine);
    }
    return data;
  }

  public static void some(String[] args) throws IOException {
    BigtableInit init = new BigtableInit();
    System.out.println("Checking status");
    boolean isPresent = init.isTable("Hello-Bigtable");
    System.out.println(isPresent);
    if (isPresent) {
      init.fetchRows("Hello-Bigtable", "rowkey-96");
    }
  }
}
