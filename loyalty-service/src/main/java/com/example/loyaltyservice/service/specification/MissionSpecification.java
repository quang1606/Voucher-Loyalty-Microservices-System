package com.example.loyaltyservice.service.specification;

import com.example.loyaltyservice.constant.MissionStatus;
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

    if (request.getPartnerId() > 0) {
      spec = spec.and(partnerIdEquals(request.getPartnerId()));
    }

    if (request.getRewardTypeValue() > 0
        && request.getRewardType() != vn.com.grpc.loyalty.entity.RewardType.UNRECOGNIZED) {
      spec = spec.and(rewardTypeEquals(RewardType.valueOf(request.getRewardType().name())));
    }

    if (request.getTaskStatusValue() > 0
        && request.getTaskStatus() != vn.com.grpc.loyalty.entity.TaskStatus.UNRECOGNIZED) {
      spec = spec.and(statusEquals(TaskStatus.valueOf(request.getTaskStatus().name())));
    }

    if (request.getMissionStatusValue() > 0
        && request.getMissionStatus() != vn.com.grpc.loyalty.entity.MissionStatus.UNRECOGNIZED) {
      String name = request.getMissionStatus().name().replaceFirst("^MS_", "");
      spec = spec.and(missionStatusEquals(MissionStatus.valueOf(name)));
    }

    return spec;
  }

  private Specification<MissionEntity> partnerIdEquals(Long partnerId) {
    return (root, query, cb) -> cb.equal(root.get("partnerId"), partnerId);
  }

  private Specification<MissionEntity> rewardTypeEquals(RewardType rewardType) {
    return (root, query, cb) -> cb.equal(root.get("rewardType"), rewardType);
  }

  private Specification<MissionEntity> statusEquals(TaskStatus status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }

  private Specification<MissionEntity> missionStatusEquals(MissionStatus missionStatus) {
    return (root, query, cb) -> cb.equal(root.get("missionStatus"), missionStatus);
  }
}
