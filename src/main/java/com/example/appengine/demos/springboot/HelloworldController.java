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

import com.google.cloud.PlatformInformation;
import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloworldController {

  private final BigtableCoreTest bigtable = new BigtableCoreTest();

  @GetMapping("test")
  public String myTest() {
    return "Hey, This is really working in my local... hooray!!";
  }

  @GetMapping("table-present/{tableName}")
  public String isTablePresent(@PathVariable("tableName") String tableName) {
    System.out.println("Checking status");
    boolean isPresent = bigtable.isTable(tableName);
    return isPresent ? "Table is already present" : "Table is not present";
  }

  @PutMapping("table-present/{tableName}")
  public String createTable(@PathVariable("tableName") String tableName) {
    if(!bigtable.isTable(tableName)){
      bigtable.createTable(tableName);
    }
    return "Table with " + tableName + " Successfully created";
  }

  @GetMapping("table-present/{tableName}/{row}")
  public List<String> printRowdata(
      @PathVariable("tableName") String tableName, @PathVariable("row") String row) {
    return bigtable.fetchRows(tableName, row);
  }

  @GetMapping("isGAEStandard7")
  public boolean isGAEStandard7(){
    return PlatformInformation.isOnGAEStandard7();
  }

  @GetMapping("isGAEStandard8")
  public boolean isGAEStandard8(){
    return PlatformInformation.isOnGAEStandard8();
  }
}
