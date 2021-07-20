const
  Path = require('path'),
  NodeModules = Path.resolve(__dirname, '../node_modules');

const config = ({ mode }) => ({

  entry: './src/deps.js',

  externals: {
    'react'           : 'React',
    'react-dom'       : 'ReactDOM',
    'react-dom/server': 'ReactDOMServer',
  },

  output: {
    filename: 'deps.js',
    path: Path.resolve(__dirname, '..', 'dist', mode),
    library: 'Deps',
    libraryTarget: 'this',
  },

  resolve: {
    modules: [
      NodeModules,
      'node_modules',
    ],
  },
  resolveLoader: {
    modules: [
      NodeModules,
    ],
  },

  bail: true,
})

module.exports = config
