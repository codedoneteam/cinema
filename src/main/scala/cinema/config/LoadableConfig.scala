package cinema.config

import java.io.File

import com.typesafe.config.{ConfigFactory, Config => TypeConfig}
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository

trait LoadableConfig  {
    private val defaultLocalPath = "config"
    private val defaultConfigFileName = "application.conf"

    def load(property: Option[String] = None, local: Option[String] = None, fileName: Option[String] = None): TypeConfig = {
      property match {
        case Some(uri) if uri.startsWith("git://") || uri.startsWith("file://") || uri.startsWith("http://") || uri.startsWith("ssh://") =>
          if (!new File(local.getOrElse(defaultLocalPath)).exists()) {
            Git.cloneRepository.setDirectory(new File(local.getOrElse(defaultLocalPath))).setURI(uri).call
          }
          else {
            val localRepo = new FileRepository(local.getOrElse(defaultLocalPath) + File.separator  + ".git")
            val git = new Git(localRepo)
            git.pull().call()
          }
          val configFile = new File(local.getOrElse(defaultLocalPath) + File.separator + fileName.getOrElse(defaultConfigFileName))
          ConfigFactory.parseFile(configFile)

        case Some(uri) =>
          ConfigFactory.parseFile(new File(uri))

        case _ => ConfigFactory.load()
      }
    }
}