package salir.fm.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import salir.fm.presentation.viewmodels.FileBrowserViewModel

val appModule = module {

    viewModel<FileBrowserViewModel> {
        FileBrowserViewModel(app = get())
    }
}