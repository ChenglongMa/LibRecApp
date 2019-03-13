#!/usr/bin/env python
# -*- coding: UTF-8 -*-
"""
This script aims to merge the experiment result file of LibRecApp.jar
"""
__author__ = "Chenglong Ma"

import argparse
import logging
import os
import time

import pandas as pd


class Properties(object):

    def __init__(self, file_name):
        self.fileName = file_name
        self.properties = {}

    def __get_dict(self, str_name, dict_name, value):

        if str_name.find('.') > 0:
            k = str_name.split('.')[0]
            dict_name.setdefault(k, {})
            return self.__get_dict(str_name[len(k) + 1:], dict_name[k], value)
        else:
            dict_name[str_name] = value
            return

    def get_properties(self):
        with open(self.fileName, "rt", encoding='utf8') as pro_file:
            for line in pro_file.readlines():
                line = line.strip().replace('\n', '')
                if line.find("#") != -1:
                    line = line[0:line.find('#')]
                if line.find('=') > 0:
                    strs = line.split('=')
                    strs[1] = line[len(strs[0]) + 1:]
                    self.properties[strs[0].strip()] = strs[1].strip()
        return self.properties


if __name__ == '__main__':
    # logger config
    logging.basicConfig(
        # filename=f'./log/{t_str}.log',
        # filemode='w',
        level=logging.DEBUG,
        format='%(asctime)s %(filename)s %(levelname)s %(message)s',
        datefmt='%H:%M:%S')
    logger = logging.getLogger(__name__)

    # cli arguments config
    arg_parser = argparse.ArgumentParser()
    arg_parser.add_argument('-r', '--recommender', nargs='+', required=True,
                            help='The results from recommender(s) applied')
    arg_parser.add_argument('-d', '--dataset', nargs='+', required=True,
                            help='The results of the dataset(s)')
    arg_parser.add_argument('-cv', type=int, default=1,
                            help='The number of folds for cross validation')
    args = arg_parser.parse_args()
    logger.info(f'Conditions:\n{str(args)}')
    recs = args.recommender
    datasets = args.dataset
    cv = args.cv
    t_str = time.strftime("%Y%m%d%H%M%S", time.localtime())
    pwd = os.getcwd() + '/'
    def_conf_name = pwd + 'conf/default.properties'
    def_prop = Properties(def_conf_name)
    def_conf = def_prop.get_properties()
    for dataset in datasets:
        data__conf_name = pwd + def_conf[f'{dataset}.properties']
        data_prop = Properties(data__conf_name)
        data_conf = data_prop.get_properties()
        # Override the properties
        for key, val in data_conf.items():
            def_conf[key] = val

        res_root_dir = pwd + def_conf['dfs.result.dir'] + '/'
        ori_path = pwd + def_conf['dfs.data.dir'] + '/' + def_conf['data.input.path']
        logger.info(f'Reading from {ori_path}')
        src_file = pd.read_csv(ori_path, sep='[ \t,]+', header=None, engine='python')[[0, 1, 2]]

        src_file.columns = ['user', 'item', 'rating']
        logger.info(f'File shape: {src_file.shape}')
        for fold in range(1, cv + 1):
            logger.info(f'Processing fold {fold}')
            data_file = src_file.copy()
            for rec in recs:
                res_dir = f'{res_root_dir}{dataset}/{ori_path.split("/").pop()}-{rec}-output/'
                res_file = pd.read_csv(f'{res_dir}{rec}-{fold}', header=None)
                res_file.columns = ['user', 'item', f'rating_{rec}']
                logger.info(f'File shape: {res_file.shape}')
                data_file = data_file.merge(res_file, on=['user', 'item'])
                logger.info(f'Merged result file shape: {data_file.shape}')
            save_as = f'{res_root_dir}{dataset}/merge_{recs}_{fold}_{t_str}.csv'
            data_file.to_csv(save_as, index=False)
            logger.info(f'Finished - result file: {save_as}')
