package com.example.appengine.demos.springboot;

import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest;
import com.google.cloud.bigtable.config.BigtableOptions;
import com.google.cloud.bigtable.core.IBigtableDataClient;
import com.google.cloud.bigtable.core.IBigtableTableAdminClient;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.grpc.BigtableSession;
import com.google.cloud.bigtable.grpc.scanner.FlatRow;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BigtableCoreTest {

  private static final String PROJECT_ID = "grass-clump-479";
  private static final String INSTANCE_ID = "shared-perf-2";
  private static final String TABLE_NAME = System.getProperty("bigtable.tableName", "");

  private final IBigtableDataClient dataClient;
  private final IBigtableTableAdminClient adminClient;

  public BigtableCoreTest(){
    try{
      BigtableOptions option = BigtableOptions.builder()
          .setProjectId(PROJECT_ID)
          .setInstanceId(INSTANCE_ID)
          .setUserAgent("Test-App-engine")
          .setUseGCJClient(false)
          .build();
      final BigtableSession session =  new BigtableSession(option);
      dataClient = session.getDataClientWrapper();
      adminClient = session.getTableAdminClientWrapper();
    } catch (IOException ex){
      System.out.println("-- IOException --");
      ex.printStackTrace();
      throw new RuntimeException();
    }
  }

  public boolean isTable(final String tableNameVal) {
    return adminClient.listTables().stream().anyMatch(t -> t.equals(tableNameVal));
  }

  public List<String> fetchRows(String tableName, String rowKey) {
    List<FlatRow> rows = dataClient.readFlatRowsList(Query.create(tableName).rowKey(rowKey));
    List<String> data = new ArrayList<>();
    rows.forEach(x -> {
      data.addAll(printResult(x));
    });
    return data;
  }

  public void createTable(String tableName){
    CreateTableRequest request = CreateTableRequest.of(tableName).addFamily("cf");
    adminClient.createTable(request);
  }

  public List<String> printResult(FlatRow flatRow) {
    List<String> rowData = new ArrayList<>();
    String row = flatRow.getRowKey().toStringUtf8();
    List<FlatRow.Cell> cells = flatRow.getCells();
    for (FlatRow.Cell cell : cells) {
      String printLine =
          "Row: " + row
              + ", Family: " + cell.getFamily()
              + ", Qualifier: " + cell.getQualifier().toStringUtf8()
              + ", Value: " + cell.getValue().toStringUtf8();
      System.out.println(printLine);
      rowData.add(printLine);
    }
    return rowData;
  }

}
