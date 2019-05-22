package com.example.appengine.demos.springboot;

import com.google.cloud.bigtable.config.BigtableOptions;
import com.google.cloud.bigtable.core.IBigtableDataClient;
import com.google.cloud.bigtable.core.IBigtableTableAdminClient;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.google.cloud.bigtable.grpc.BigtableSession;
import com.google.cloud.bigtable.grpc.scanner.FlatRow;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddDataInBigtableTable {

  private static final String PROJECT_ID = "grass-clump-479";
  private static final String INSTANCE_ID = "shared-perf-2";
  private static final String APP_PROFILE_ID = System.getProperty("ae.profile", "java-hbase");

  private final IBigtableDataClient dataClient;
  private final IBigtableTableAdminClient adminClient;

  private AddDataInBigtableTable(){
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

  public static void main(String[] args){
    AddDataInBigtableTable basic = new AddDataInBigtableTable();
    String tableId = "AppEngineTestTable";
//
//    CreateTableRequest createTableRequest =
//        CreateTableRequest.of("AppEngineTestTable").addFamily("cf").addFamily("cf1");
//    Table table = basic.adminClient.createTable(createTableRequest);
//    System.out.println("Table created:  " + table.getId());
    RowMutation rowMutation1 = RowMutation.create(tableId, "test-row")
        .setCell("cf", "qualifier-1", "this could be a large value")
        .setCell("cf", "qualifier-2", "this could be another value")
        .setCell("cf1", "qualifier", "this is large value");

    basic.dataClient.mutateRow(rowMutation1);
    basic.dataClient.mutateRow(RowMutation.create(tableId, "another-row")
        .setCell("cf", "qualifier", "this could be a large value"));

    System.out.println("-----------");
    basic.fetchRows(tableId, "test-row");
    basic.fetchRows(tableId, "another-row");

  }

  private List<String> fetchRows(String tableName, String rowKey) {
    List<FlatRow> rows = dataClient.readFlatRowsList(Query.create(tableName).rowKey(rowKey));
    List<String> data = new ArrayList<>();
    rows.forEach(x -> {
      data.addAll(printResult(x));
    });
    return data;
  }

  private List<String> printResult(FlatRow flatRow) {
    List<String> rowData = new ArrayList<>();
    String row = flatRow.getRowKey().toStringUtf8();
    List<FlatRow.Cell> cells = flatRow.getCells();
    for (FlatRow.Cell cell : cells) {
      String printLine =
          "Row: " + row + ", Family: " + cell.getFamily() + ", Qualifier: " + cell.getQualifier().toStringUtf8()
              + ", " + "Value: " + cell.getValue().toStringUtf8();
      System.out.println(printLine);
      rowData.add(printLine);
    }
    return rowData;
  }

}
