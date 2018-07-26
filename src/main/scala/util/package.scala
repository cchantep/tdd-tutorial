/*
 * Copyright 2012-2013 Stephane Godbillon (@sgodbillon) and Zenexity
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactivemongo

import java.io.InputStream

import java.net.URI

import scala.util.control.NonFatal

package object util {
  /** Makes an option of the value matching the condition. */
  def option[T](cond: => Boolean, value: => T): Option[T] =
    if (cond) Some(value) else None

  def withContent[T](uri: URI)(f: InputStream => T): T = {
    lazy val in = if (uri.getScheme == "classpath") {
      Thread.currentThread().getContextClassLoader.
        getResourceAsStream(uri.getPath)

    } else {
      uri.toURL.openStream()
    }

    try {
      f(in)
    } catch {
      case NonFatal(cause) => throw cause
    } finally {
      in.close()
    }
  }
}
