package pl.muninn.hocones.cli

import cats.data.Kleisli
import cats.effect.Console.io.{putError, putStrLn}
import cats.effect.IO
import cats.implicits._
import com.monovore.decline.Help
import fansi.Color

object ApplicationHelp {

  import pl.muninn.hocones.cli.show.showStr

  private def displayHelpErrors: Help => IO[Unit] = { help =>
    IO.pure(help.errors)
      .flatMap(
        errors =>
          errors
            .map(error => Color.Red(error))
            .map(str => putError(str))
            .sequence
      ) *> IO.unit
  }

  val displayHelp: Kleisli[IO, Help, Unit] = {
    Kleisli.ask[IO, Help].mapF { help =>
      for {
        help <- help
        _ <- displayHelpErrors(help)
        _ <- IO(help.copy(errors = Nil)).flatMap(helpWithoutErrors => putStrLn(helpWithoutErrors.toString()))
      } yield ()
    }
  }
}
