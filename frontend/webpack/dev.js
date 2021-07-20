const
  WebpackMerge = require('webpack-merge').merge,
  common = require('./common');

const ctx = {
  mode: 'dev',
}

const config = WebpackMerge(common(ctx), {
  mode: 'development',
});

module.exports = config;
