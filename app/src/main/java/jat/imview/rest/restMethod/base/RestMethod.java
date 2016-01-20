package jat.imview.rest.restMethod.base;

import jat.imview.rest.resource.base.Resource;

/**
 * Created by bulat on 23.12.15.
 */
public interface RestMethod<T extends Resource> {
    public RestMethodResult<T> execute();
}
