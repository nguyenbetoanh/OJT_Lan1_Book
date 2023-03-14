package ra.model.service;

import ra.dto.request.StarRequest;
import ra.model.entity.Star;

public interface StarService extends RootService<Star,Integer>{
    Star mapRequestToStar(Integer userId,StarRequest request);
    Star findByBookIdAndUserId(Integer bookId, Integer userId);
}
