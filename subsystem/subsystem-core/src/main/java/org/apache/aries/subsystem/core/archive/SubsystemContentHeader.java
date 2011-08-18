/*
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
package org.apache.aries.subsystem.core.archive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

public class SubsystemContentHeader extends AbstractHeader {
	public static class Content {
		private final boolean mandatory;
		private final String name;
		private final String type;
		private final VersionRange versionRange;
		
		public Content(boolean mandatory, String name, String type, VersionRange versionRange) {
			this.mandatory = mandatory;
			this.name = name;
			this.type = type;
			this.versionRange = versionRange;
		}
		
		public String getName() {
			return name;
		}
		
		public String getType() {
			return type;
		}
		
		public VersionRange getVersionRange() {
			return versionRange;
		}
		
		public boolean isMandatory() {
			return mandatory;
		}
		
		public String toString() {
			return new StringBuilder(getName())
				.append(';')
				.append(VersionAttribute.NAME)
				.append('=')
				.append(getVersionRange())
				.append(';')
				.append(TypeAttribute.NAME)
				.append("=")
				.append(getType())
				.append(';')
				.append(ResolutionDirective.NAME)
				.append(":=")
				.append(isMandatory())
				.toString();
		}
	}
	
	// TODO Add to constants.
	public static final String NAME = "Subsystem-Content";
	
	private final Collection<Content> contents;
	
	public SubsystemContentHeader(String value) {
		super(NAME, value);
		contents = new ArrayList<Content>(clauses.size());
		for (Clause clause : clauses) {
			boolean mandatory = true;
			Directive directive = clause.getDirective(ResolutionDirective.NAME);
			if (directive != null)
				mandatory = ((ResolutionDirective)directive).isMandatory();
			String name = clause.getPath();
			// TODO Assumes all resources are bundles.
			String type = TypeAttribute.DEFAULT_VALUE;
			Attribute attribute = clause.getAttribute(TypeAttribute.NAME);
			if (attribute != null)
				type = ((TypeAttribute)attribute).getType();
			VersionRange versionRange = new VersionRange(Version.emptyVersion.toString());
			attribute = clause.getAttribute(Constants.VERSION_ATTRIBUTE);
			if (attribute != null) {
				versionRange = new VersionRange(attribute.getValue());
			}
			contents.add(new Content(mandatory, name, type, versionRange));
		}
	}

	public Collection<Content> getContents() {
		return Collections.unmodifiableCollection(contents);
	}
}
