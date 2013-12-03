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

import ua.bitternet.docflow.domain.Person

/**
 * Person Service
 *
 * @author Yves Zoundi
 */
interface DataService {
  /**
   * Persists a person
   * @param domainObject A domain object
   */
  void save(Object domainObject)

  /**
   * Persists a person
   * @param domainObject A domain object
   */
  void saveOrFail(Object domainObject)

  /**
   * Deletes a domainObject
   * @param domainObject A domain object
   */
  void delete(Object domainObject)

  /**
   * Validates the domain object using domain constraints
   * @param domainObject A domain object
   */
  boolean validate(Object domainObject)

  /**
   * Returns all domain objects
   * @return All domain objects in the database
   */
  public List<Object> findAll(Class<Object> clazz)
}
