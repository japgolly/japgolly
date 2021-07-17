const
  Path = require('path'),
  Webpack = require('webpack'),
  TerserPlugin = require('terser-webpack-plugin'),
  NodeModules = Path.resolve(__dirname, '../node_modules');

module.exports = {

  entry: './src/deps.js',

  externals: {
    'react'           : 'React',
    'react-dom'       : 'ReactDOM',
    'react-dom/server': 'ReactDOMServer',
  },

  output: {
    filename: 'deps.js',
    path: Path.resolve(__dirname, 'dist'),
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

  mode: 'production',

  performance: {
    hints: false
  },

  optimization: {
    minimizer: [new TerserPlugin({
      parallel: true,
      terserOptions: {
        output: {
          comments: false,
        }
      },
    })]
  },

  plugins: [
    new Webpack.LoaderOptionsPlugin({
      minimize: true,
    }),
  ],

  bail: true,
}
