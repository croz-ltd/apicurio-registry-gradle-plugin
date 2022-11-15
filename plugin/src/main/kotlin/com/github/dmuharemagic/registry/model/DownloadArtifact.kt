package com.github.dmuharemagic.registry.model

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * A DSL structure representing the download action specifics.
 *
 * @property outputPath The directory to where the remote file should be downloaded to.
 * @property outputFileName Customizable file name of the remote file to be downloaded. The artifact name fetched from the schema registry is applied if omitted.
 */
open class DownloadArtifact : Artifact() {
    @get:Input
    lateinit var outputPath: String

    @get:Input
    @get:Optional
    var outputFileName: String? = null
}