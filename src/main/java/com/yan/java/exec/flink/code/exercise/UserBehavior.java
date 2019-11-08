package com.yan.java.exec.flink.code.exercise;

import java.util.Objects;

public class UserBehavior {
  public long userId;
  public long itemId;
  public int categoryId;
  public String behavior;
  public long timestamp;

  public UserBehavior(long userId, long itemId, int categoryId, String behavior, long timestamp) {
    this.userId = userId;
    this.itemId = itemId;
    this.categoryId = categoryId;
    this.behavior = behavior;
    this.timestamp = timestamp;
  }

  public UserBehavior() {
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public long getItemId() {
    return itemId;
  }

  public void setItemId(long itemId) {
    this.itemId = itemId;
  }

  public int getcategoryId() {
    return categoryId;
  }

  public void setcategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public String getBehavior() {
    return behavior;
  }

  public void setBehavior(String behavior) {
    this.behavior = behavior;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public String toString() {
    return "UserBehavior{" +
        "userId=" + userId +
        ", itemId=" + itemId +
        ", categoryId=" + categoryId +
        ", behavior='" + behavior + '\'' +
        ", timestamp=" + timestamp +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserBehavior that = (UserBehavior) o;
    return userId == that.userId &&
        itemId == that.itemId &&
        categoryId == that.categoryId &&
        timestamp == that.timestamp &&
        Objects.equals(behavior, that.behavior);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, itemId, categoryId, behavior, timestamp);
  }
}
