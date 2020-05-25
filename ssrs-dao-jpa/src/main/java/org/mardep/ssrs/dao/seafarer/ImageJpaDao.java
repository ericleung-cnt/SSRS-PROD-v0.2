package org.mardep.ssrs.dao.seafarer;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.seafarer.IImageDao;
import org.mardep.ssrs.domain.seafarer.Image;
import org.springframework.stereotype.Repository;

@Repository
public class ImageJpaDao extends AbstractJpaDao<Image, Long> implements IImageDao {

}
