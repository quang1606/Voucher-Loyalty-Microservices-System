package com.example.loyaltyservice.service.specification;

import com.example.loyaltyservice.constant.RewardType;
import com.example.loyaltyservice.constant.TaskStatus;
import com.example.loyaltyservice.entity.MissionEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import vn.com.grpc.loyalty.entity.SearchMissionRequest;

@Component
public class MissionSpecification {

  public Specification<MissionEntity> createSpecification(SearchMissionRequest request) {
    Specification<MissionEntity> spec = Specification.where(null);

    request.getNameStore();
    if (!request.getNameStore().trim().isEmpty()) {
      spec = spec.and(nameStoreLike(request.getNameStore()));
    }

    if (request.getRewardType() != vn.com.grpc.loyalty.entity.RewardType.UNRECOGNIZED) {
      spec = spec.and(rewardTypeEquals(RewardType.valueOf(request.getRewardType().name())));
    }

    if (request.getTaskStatus() != vn.com.grpc.loyalty.entity.TaskStatus.UNRECOGNIZED) {
      spec = spec.and(statusEquals(TaskStatus.valueOf(request.getTaskStatus().name())));
    }

    return spec;
  }

  private Specification<MissionEntity> nameStoreLike(String nameStore) {
    return (root, query, cb) ->
        cb.like(
            cb.lower(root.get("nameStore")),
            "%" + nameStore.trim().toLowerCase() + "%"
        );
  }

  private Specification<MissionEntity> rewardTypeEquals(RewardType rewardType) {
    return (root, query, cb) -> cb.equal(root.get("rewardType"), rewardType);
  }

  private Specification<MissionEntity> statusEquals(TaskStatus status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }
}
