package com.thfw.robotheart.view;

import me.samlss.bloom.particle.BloomParticle;
import me.samlss.bloom.shape.ParticleRectShape;
import me.samlss.bloom.shape.ParticleShape;
import me.samlss.bloom.shape.distributor.RectShapeDistributor;

/**
 * Author:pengs
 * Date: 2022/1/18 14:17
 * Describe:Todo
 */
public class MeRectShapeDistributor extends RectShapeDistributor {


    @Override
    public ParticleShape getShape(BloomParticle particle) {
        return new ParticleRectShape(0, 0, particle.getInitialX(), particle.getInitialY(), particle.getRadius());
    }


}
