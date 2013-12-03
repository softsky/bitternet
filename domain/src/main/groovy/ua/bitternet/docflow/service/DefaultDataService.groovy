/*
 * Copyright 2013 Rimero Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ua.bitternet.docflow.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Default person service implementation
 *
 * @author Yves Zoundi
 */
@Service("dataService")
@Transactional
class DefaultDataService implements DataService {
  void save(final Object domainObject) {
    domainObject.save(flush:true)
  }

  void saveOrFail(final Object domainObject) {
    assert domainObject.validate(): domainObject.errors.allErrors
    domainObject.save(flush:true)
  }

  void delete(final Object domainObject) {
    domainObject.delete(flush:true)
  }

  boolean validate(final Object domainObject) {
    domainObject.validate()
  }

  public List<Object> findAll(Class<Object> clazz) {
    clazz.findAll()
  }
}