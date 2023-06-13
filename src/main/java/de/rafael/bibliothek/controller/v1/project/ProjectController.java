/*
 * MIT License
 *
 * Copyright (c) 2023 Rafael
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.rafael.bibliothek.controller.v1.project;

import de.rafael.bibliothek.classes.ApiController;
import de.rafael.bibliothek.configuration.AppConfiguration;
import de.rafael.bibliothek.database.model.Group;
import de.rafael.bibliothek.database.model.Project;
import de.rafael.bibliothek.database.model.Version;
import de.rafael.bibliothek.database.repository.BuildRepository;
import de.rafael.bibliothek.database.repository.GroupRepository;
import de.rafael.bibliothek.database.repository.ProjectRepository;
import de.rafael.bibliothek.database.repository.VersionRepository;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rafael K.
 * @since 19:46, 12.06.23
 */

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController extends ApiController {

    private static final CacheControl CACHE = defaultCache();

    @Autowired
    public ProjectController(AppConfiguration configuration, ProjectRepository projects, VersionRepository versions, GroupRepository groups, BuildRepository builds) {
         super(configuration, projects, versions, groups, builds);
    }

    @GetMapping("/v1/projects/{project:" + Project.PATTERN + "}")
    public ResponseEntity<?> project(@PathVariable("project") @Pattern(regexp = Project.PATTERN) String projectName) {
        var project = super.findProject(projectName);
        var groups = super.findGroupsAsStream(project).map(Group::name).toList();
        var versions = super.findVersionsAsStream(project).map(Version::name).toList();
        return ok(
                CACHE,
                new Response(
                    project.name(),
                    project.friendlyName(),
                    groups,
                    versions
                )
        );
    }

    private record Response(String project_id, String project_name, List<String> groups, List<String> versions) {}

}
